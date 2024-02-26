package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select count(*) from setmeal where category_id = #{id}")
    Integer getCountByCategoryId(Long id);


    @AutoFill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    Long setmealPageQueryCount(SetmealPageQueryDTO setmealPageQueryDTO);


    List<SetmealVO> setmealPageQuery(int startPageNum, int pageSize, String name, Integer status, Integer categoryId);
}
