package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    void deleteBySetmealIds(Long[] ids);

    List<SetmealDish> queryBySetmealId(Long id);

    @Select("select setmeal_dish.copies,dish.name,dish.image,dish.description from setmeal_dish left join dish " +
            "on setmeal_dish.dish_id = dish.id where setmeal_dish.setmeal_id = #{id}")
    List<DishItemVO> queryDishBySetmealId(Long id);
}
