package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkspaceService {
    BusinessDataVO getBusinessData();

    OrderOverViewVO getOverviewOrders();

    DishOverViewVO getOverviewDishes();

    SetmealOverViewVO getOverviewSetmeals();

    BusinessDataVO getBusinessDataBatch(LocalDateTime of, LocalDateTime of1);
}
