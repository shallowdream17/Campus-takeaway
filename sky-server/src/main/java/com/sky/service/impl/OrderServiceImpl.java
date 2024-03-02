package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常（地址簿为空 - 购物车为空）
        AddressBook addressBook = addressBookMapper.queryAddressById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.lookShoppingCart(userId);
        if(shoppingCartList == null || shoppingCartList.size() < 1){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orderMapper.addOrder(orders);
        //向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart spc:shoppingCartList){
             OrderDetail orderDetail = new OrderDetail();
             BeanUtils.copyProperties(spc,orderDetail);
             orderDetail.setOrderId(orders.getId());
             orderDetailList.add(orderDetail);
        }
        orderDetailMapper.addOrderDetail(orderDetailList);
        //清空购物车
        shoppingCartMapper.cleanShoppingCart(userId);
        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 用户成功支付
     * @param ordersPaymentDTO
     */
    @Override
    public void paymentSuccess(OrdersPaymentDTO ordersPaymentDTO) {
        //先查询该订单是否存在且未支付
        Orders orders = orderMapper.queryByOrderNumber(ordersPaymentDTO.getOrderNumber());
        //若订单不存在
        if(orders == null){
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        //若该订单存在，但是已经支付过了
        if(orders.getPayStatus()==Orders.PAID){
            throw new OrderBusinessException("订单已支付!");
        }
        //支付成功
        Orders successOrder = Orders.builder()
                .id(orders.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.updateOrder(successOrder);
    }

    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult queryHistoryOrders(int page, int pageSize, Integer status) {
        Long userId = BaseContext.getCurrentId();
        Long count = orderMapper.queryHistoryOrdersCount(userId,status);
        int startPageNum = (page - 1) * pageSize;
        List<Orders> ordersList = orderMapper.queryHistoryOrders(startPageNum,pageSize,status,userId);
        List<OrderVO> orderVOList = new ArrayList<>();
        for(Orders ol:ordersList){
            List<OrderDetail> orderDetailList = orderDetailMapper.queryOrderId(ol.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(ol,orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(count);
        pageResult.setRecords(orderVOList);
        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO queryOrderDetail(Long id) {
        Orders orders = orderMapper.queryById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.queryOrderId(orders.getId());
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }
}
