package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //先判断参数是否正确
        judgeParameters(begin,end);

        //获取日期list
        List<LocalDate> dateList = getDateList(begin,end);

        //计算每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("status",5);
            map.put("begin",beginTime);
            map.put("end",endTime);
            Double turnover = reportMapper.getTurnoverStatistics(map);
            //细节
            if(turnover==null){
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }

        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        judgeParameters(begin,end);
        List<LocalDate> dateList = getDateList(begin,end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("end",endTime);
            Integer totalUserCount = userMapper.getUserStatistics(map);
            map.put("begin",beginTime);
            Integer newUserCount = userMapper.getUserStatistics(map);
            newUserList.add(newUserCount);
            totalUserList.add(totalUserCount);
        }

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
        return userReportVO;
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        judgeParameters(begin,end);
        List<LocalDate> dateList = getDateList(begin,end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer allOrderCount = 0;
        Integer allValidOrderCount = 0;
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer orderCount = orderMapper.getOrderStatistics(map);
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderStatistics(map);
            allOrderCount+=orderCount;
            allValidOrderCount+=validOrderCount;
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        Double orderCompletedRate = 0.0;
        if(allOrderCount!=0){
            orderCompletedRate = allValidOrderCount.doubleValue()/allOrderCount.doubleValue();
        }
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCompletionRate(orderCompletedRate)
                .orderCountList(StringUtils.join(orderCountList,","))
                .totalOrderCount(allOrderCount)
                .validOrderCount(allValidOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .build();
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        judgeParameters(begin,end);
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getTop10(beginTime,endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for(GoodsSalesDTO gsd:goodsSalesDTOList){
            nameList.add(gsd.getName());
            numberList.add(gsd.getNumber());
        }
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
        return salesTop10ReportVO;
    }

    private void judgeParameters(LocalDate begin, LocalDate end) {
        //begin大于end，抛出异常
        if(begin.compareTo(end)>0){
            throw new RuntimeException("参数错误");
        }
        Long dayDifference = begin.until(end, ChronoUnit.DAYS);
        //起始天数与结束天数相差1年以上，抛出异常
        if(dayDifference > 366 ){
            throw new RuntimeException("参数错误");
        }
    }

    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while(begin.compareTo(end)<=0){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        return dateList;
    }


}
