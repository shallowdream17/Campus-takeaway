package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    void paymentSuccess(OrdersPaymentDTO ordersPaymentDTO);

    PageResult queryHistoryOrders(int page, int pageSize, Integer status);

    OrderVO queryOrderDetail(Long id);

    void cancelOrder(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getOrderQuantity();

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void deliveryOrder(Long id);

    void finishOrder(Long id);

    void remindOrder(Long id);
}
