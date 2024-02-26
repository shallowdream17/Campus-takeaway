package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 查询dish_id in 数组ids 中的套餐个数
     * @param ids
     * @return
     */
    Long queryCountByDishId(Long[] ids);

    void addSetmealDish(List<SetmealDish> setmealDishes);
}
