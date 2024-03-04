package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    Long conditionSearchCount(OrdersPageQueryDTO ordersPageQueryDTO);

    List<Orders> conditionSearch(int startPageNum,OrdersPageQueryDTO opqd);

    @Select("select count(*) from orders where status = #{status}")
    Integer queryStatusQuantity(Integer status);

    /**
     * 根据订单状态和订单时间查询订单
     * @param status
     * @param now
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{now}")
    List<Orders> getByStatusAndTimeLT(Integer status, LocalDateTime now);

    /**
     * 查询订单总数和有效订单数 数据
     * @param map
     * @return
     */
    Integer getOrderStatistics(Map map);

    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);
}
