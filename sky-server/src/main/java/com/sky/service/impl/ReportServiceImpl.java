package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //begin大于end，抛出异常
        if(begin.compareTo(end)>0){
            throw new RuntimeException("参数错误");
        }
        Long dayDifference = begin.until(end, ChronoUnit.DAYS);
        //起始天数与结束天数相差1年以上，抛出异常
        if(dayDifference > 366 ){
            throw new RuntimeException("参数错误");
        }

        List<LocalDate> dateList = new ArrayList<>();
        while(begin.compareTo(end)<=0){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

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
}
