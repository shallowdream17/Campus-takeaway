package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public  interface OrderMapper {

    void addOrder(Orders orders);

    @Select("select * from orders where number = #{orderNumber}")
    Orders queryByOrderNumber(String orderNumber);

    void updateOrder(Orders successOrder);

    Long queryHistoryOrdersCount(Long userId, Integer status);

    List<Orders> queryHistoryOrders(int startPageNum, int pageSize, Integer status,Long userId);

    @Select("select * from orders where id = #{id}")
    Orders queryById(Long id);
}
