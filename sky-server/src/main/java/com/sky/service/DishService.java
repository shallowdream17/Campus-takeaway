package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {

    void addDish(DishDTO dishDTO);

    PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO);

    void dishDelete(Long[] ids);

    DishVO queryById(Long id);

    void updateById(DishDTO dishDTO);

    void enableAndDisableDish(Integer status, Long id);

    List<Dish> queryByCategoryId(Long categoryId);
}
