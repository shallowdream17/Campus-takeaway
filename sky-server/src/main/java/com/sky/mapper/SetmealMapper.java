package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    @Select("select count(*) from setmeal where category_id = #{id}")
    Integer getCountByCategoryId(Long id);


    @AutoFill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);
}
