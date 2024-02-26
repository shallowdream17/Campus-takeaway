package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {


    @Select("select  count(*) from dish where category_id = #{id}")
    Integer getCountByCategoryId(Long id);


    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void addDish(Dish dish);

    /**
     * 查询满足条件的菜品的数量
     * @param dishPageQueryDTO
     * @return
     */
    Long pageQueryCount(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品分页查询
     * @param startPage
     * @param pageSize
     * @param name
     * @param status
     * @param categoryId
     * @return
     */
    List<DishVO> pageQuery(int startPage, int pageSize, String name, Integer status, Integer categoryId);

    /**
     * 查询id为ids中的在售菜品个数
     * @param ids
     * @return
     */
    Long queryOnSaleDishCount(Long[] ids);

    /**
     * 根据id删除菜品
     * @param ids
     */
    void deleteById(Long[] ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    Dish queryById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> queryByCategoryId(Long categoryId);
}
