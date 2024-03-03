package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrders(){
        log.info("处理超时订单:{}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now().plusMinutes(-15);

        List<Orders> ordersList = orderMapper.getByStatusAndTimeLT(Orders.PENDING_PAYMENT,now);
        if(ordersList!=null && ordersList.size()>0){
            for(Orders od:ordersList){
                od.setStatus(Orders.CANCELLED);
                od.setCancelTime(LocalDateTime.now());
                od.setCancelReason("订单超时，自动取消");
                orderMapper.updateOrder(od);
            }
        }
    }

    @Scheduled(cron = "0 0 4 * * ? ")
    public void processCompletedOrders(){
        log.info("定时处理处于派送中的订单{}",LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now().plusMinutes(-50);

        List<Orders> ordersList = orderMapper.getByStatusAndTimeLT(Orders.DELIVERY_IN_PROGRESS,now);
        if(ordersList!=null && ordersList.size()>0){
            for(Orders od:ordersList){
                od.setStatus(Orders.COMPLETED);
                orderMapper.updateOrder(od);
            }
        }
    }

}
