package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
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
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private WebSocketServer webSocketServer;

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
        orders.setAddress(addressBook.getDetail());
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

        //通过websocket向客户端浏览器推送消息 type orderId content
        Map map = new HashMap<>();
        map.put("type",1);//1标识来单提醒，2标识催单
        map.put("orderId",orders.getId());
        map.put("content","订单号: "+ordersPaymentDTO.getOrderNumber());
        String json = JSON.toJSONString(map);
        log.info("来单提醒已发送!");
        webSocketServer.sendToAllClient(json);

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

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {
        Orders orders = orderMapper.queryById(id);
        //订单不存在 无法取消订单
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        //只有前两种状态能取消
        if(orders.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders newOrder = new Orders();
        newOrder.setId(orders.getId());
        //待接单状态下的订单取消后需要退款
        if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            //这里直接退款,将其状态设置为退款
            newOrder.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setCancelTime(LocalDateTime.now());
        newOrder.setCancelReason("用户取消");
        orderMapper.updateOrder(newOrder);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.queryOrderId(id);
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        Long userId = BaseContext.getCurrentId();
        for(OrderDetail odl:orderDetailList){
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(odl,shoppingCart,"id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }
        // 购物车批量插入
        shoppingCartMapper.addBatch(shoppingCartList);
    }


    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
         Long count = orderMapper.conditionSearchCount(ordersPageQueryDTO);
         int startPageNum = (ordersPageQueryDTO.getPage()-1)*ordersPageQueryDTO.getPageSize();
         List<Orders> ordersList = orderMapper.conditionSearch(startPageNum,ordersPageQueryDTO);
         List<OrderVO> orderVOList = new ArrayList<>();
         if(ordersList!=null&&ordersList.size()>0){
             for(Orders odl:ordersList){
                 OrderVO orderVO = new OrderVO();
                 List<OrderDetail> orderDetailList = orderDetailMapper.queryOrderId(odl.getId());
                 BeanUtils.copyProperties(odl,orderVO);
                 String orderDishes = getOrderDishes(orderDetailList);
                 orderVO.setOrderDishes(orderDishes);
                 orderVOList.add(orderVO);
             }
         }

         PageResult pageResult = new PageResult();
         pageResult.setTotal(count);
         pageResult.setRecords(orderVOList);
         return pageResult;
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO getOrderQuantity() {
        //待接单
        Integer toBeConfirmedQuantity = orderMapper.queryStatusQuantity(Orders.TO_BE_CONFIRMED);
        //待派送
        Integer confirmedQuantity = orderMapper.queryStatusQuantity(Orders.CONFIRMED);
        //派送中
        Integer deliveryInProgressQuantity = orderMapper.queryStatusQuantity(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmedQuantity);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedQuantity);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressQuantity);
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.updateOrder(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.queryById(ordersRejectionDTO.getId());
        //只有待接单状态下可以拒单
        if(orders==null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //如果已经付款，需要进行退款
        if(orders.getPayStatus()==Orders.PAID){
            //退款操作略...
        }
        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        Orders newOrder = new Orders();
        newOrder.setId(orders.getId());
        newOrder.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setCancelTime(LocalDateTime.now());
        orderMapper.updateOrder(newOrder);
    }

    /**
     * 商户取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.queryById(ordersCancelDTO.getId());
        //订单不存在无法取消
        if(orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //如果已经付款则需要退款
        if(orders.getPayStatus()==Orders.PAID){
            //退款操作略...
        }
        Orders newOrder = new Orders();
        newOrder.setId(orders.getId());
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setCancelReason(ordersCancelDTO.getCancelReason());
        newOrder.setCancelTime(LocalDateTime.now());
        orderMapper.updateOrder(newOrder);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void deliveryOrder(Long id) {
        Orders orders = orderMapper.queryById(id);
        //订单不存在或者订单不处于已接单状态
        if(orders==null || !orders.getStatus().equals(Orders.CONFIRMED)){
            throw  new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders newOrder = new Orders();
        newOrder.setId(id);
        //修改订单状态为派送中
        newOrder.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.updateOrder(newOrder);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void finishOrder(Long id) {
        Orders orders = orderMapper.queryById(id);
        //订单不存在或订单状态不是派送中
        if(orders==null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw  new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders newOrder = new Orders();
        newOrder.setId(id);
        newOrder.setStatus(Orders.COMPLETED);
        newOrder.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateOrder(newOrder);
    }

    /**
     * 组合菜品信息字符串
     * @param orderDetailList
     * @return
     */
    private String getOrderDishes(List<OrderDetail> orderDetailList) {
        if(orderDetailList!=null&&orderDetailList.size()>0){
            String result = new String();
            for(OrderDetail odd:orderDetailList){
                result += odd.getName()+"*"+odd.getNumber()+";";
            }
            return result;
        }
        return null;
    }
}
