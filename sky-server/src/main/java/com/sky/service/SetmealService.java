package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(Long[] ids);

    void enableAndDisableSetmeal(Integer status, Long id);
}
