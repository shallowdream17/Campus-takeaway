package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional //涉及到多个数据库操作，在这添加事务，保证要么都成功，要么都失败
    public void addDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.addDish(dish);

        //获取insert语句执行后生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            for(DishFlavor df:flavors){
                df.setDishId(dishId);
            }
            dishFlavorMapper.addDishFlavor(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Long count = dishMapper.pageQueryCount(dishPageQueryDTO);
        int startPage = (dishPageQueryDTO.getPage()-1)*dishPageQueryDTO.getPageSize() ;
        List<DishVO> dishList = dishMapper.pageQuery(startPage,dishPageQueryDTO.getPageSize(),dishPageQueryDTO.getName(),dishPageQueryDTO.getStatus(),dishPageQueryDTO.getCategoryId());
        PageResult pageResult = new PageResult(count,dishList);
        return pageResult;
    }


    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void dishDelete(Long[] ids) {
        //查询ids中在售的菜品个数
        Long onSaleDishCount = dishMapper.queryOnSaleDishCount(ids);
        if(onSaleDishCount>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //查询ids中关联的套餐个数
        Long dishInSetmealCount = setmealDishMapper.queryCountByDishId(ids);
        if(dishInSetmealCount!=0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //先删除菜品关联的口味
        dishFlavorMapper.deleteByDishId(ids);
        //删除菜品
        dishMapper.deleteById(ids);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO queryById(Long id) {
        List<DishFlavor> dishFlavorList = dishFlavorMapper.queryByDishId(id);
        Dish dish = dishMapper.queryById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    /**
     * 根据id修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateById(DishDTO dishDTO) {
        //先删除掉原有的口味
        dishFlavorMapper.deleteByDishId(new Long[]{dishDTO.getId()});

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateById(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.addDishFlavor(flavors);
        }
    }

    /**
     * 起售、停售菜品
     * @param status
     * @param id
     */
    @Override
    public void enableAndDisableDish(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> queryByCategoryId(Long categoryId) {
        List<Dish> list = dishMapper.queryByCategoryId(categoryId);
        return list;
    }

    /**
     * C端-根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> queryDishByCategoryIdInC(Long categoryId) {
        List<Dish> dishList = dishMapper.queryByCategoryId(categoryId);

        List<DishVO> dishVOList = new ArrayList<>();

        for(Dish dish:dishList){
            List<DishFlavor> dishFlavorList = dishFlavorMapper.queryByDishId(dish.getId());

            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish,dishVO);
            dishVO.setFlavors(dishFlavorList);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * C端-根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> queryDishBySetmealId(Long id) {
        List<DishItemVO> dishItemVOList = setmealDishMapper.queryDishBySetmealId(id);
        return dishItemVOList;
    }
}
