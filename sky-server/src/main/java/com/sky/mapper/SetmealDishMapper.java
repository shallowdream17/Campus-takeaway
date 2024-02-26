package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {

    /**
     * 查询dish_id in 数组ids 中的套餐个数
     * @param ids
     * @return
     */
    Long queryCountByDishId(Long[] ids);
}
