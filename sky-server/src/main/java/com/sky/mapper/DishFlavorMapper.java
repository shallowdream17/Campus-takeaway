package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    void addDishFlavor(List<DishFlavor> flavors);

    void deleteByDishId(Long[] ids);

    List<DishFlavor> queryByDishId(Long id);
}
