package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(Long[] ids);

    void enableAndDisableSetmeal(Integer status, Long id);

    SetmealVO querySetmealById(Long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    List<Setmeal> queryByCategoryIdInC(Long categoryId);
}
