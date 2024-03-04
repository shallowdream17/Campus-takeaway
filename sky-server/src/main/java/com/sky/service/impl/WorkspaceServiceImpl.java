package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getMap;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        Map map = getNeedMap();
        //计算当日新增用户
        Integer newUsers = userMapper.getUserStatistics(map);
        //计算当日订单完成率
        Integer orderCount = orderMapper.getOrderStatistics(map);
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = orderMapper.getOrderStatistics(map);
        double orderCompletionRate = 0.0;
        if(orderCount!=0){
            orderCompletionRate = validOrderCount.doubleValue()/orderCount.doubleValue();
        }
        //计算当日营业额
        Double turnover = reportMapper.getTurnoverStatistics(map);
        //计算平均客单价
        Double unitPrice = 0.0;
        if(validOrderCount!=0){
            unitPrice = turnover/validOrderCount.doubleValue();
        }
        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .build();
        return businessDataVO;
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        Map map = getNeedMap();
        //全部订单
        Integer allOrders = orderMapper.getOrderStatistics(map);
        //已取消数量
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.getOrderStatistics(map);
        //已完成数量
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.getOrderStatistics(map);
        //待派送数量
        map.put("status",Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getOrderStatistics(map);
        //待接单数量
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.getOrderStatistics(map);
        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
        return orderOverViewVO;
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        // TODO 常量修改
        Integer discontinued = dishMapper.getDishCountByStatus(0);
        Integer sold = dishMapper.getDishCountByStatus(1);
        DishOverViewVO dishOverViewVO = DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
        return dishOverViewVO;
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        // TODO 常量修改
        Integer discontinued = setmealMapper.getSetmealCountByStatus(0);
        Integer sold = setmealMapper.getSetmealCountByStatus(1);
        SetmealOverViewVO setmealOverViewVO = SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
        return setmealOverViewVO;
    }


    private Map getNeedMap() {
        LocalDate today = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(today, LocalTime.MAX);
        Map map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        return map;
    }


}
