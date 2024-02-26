package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.addSetmeal(setmeal);

        Long setmealId = setmeal.getId();


        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null && setmealDishes.size()>0){
            for(SetmealDish st:setmealDishes){
                st.setSetmealId(setmealId);
            }
            setmealDishMapper.addSetmealDish(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @return
     */
    @Override
    public PageResult setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Long count = setmealMapper.setmealPageQueryCount(setmealPageQueryDTO);
        int startPageNum = (setmealPageQueryDTO.getPage() - 1) * setmealPageQueryDTO.getPageSize();
        List<SetmealVO> setmealVOList = setmealMapper.setmealPageQuery(startPageNum,setmealPageQueryDTO.getPageSize(),setmealPageQueryDTO.getName(),setmealPageQueryDTO.getStatus(),setmealPageQueryDTO.getCategoryId());
        PageResult pageResult = new PageResult();
        pageResult.setTotal(count);
        pageResult.setRecords(setmealVOList);
        return pageResult;
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteSetmeal(Long[] ids) {
        int setmealOnSaleCount = setmealMapper.queryOnSaleCountByIds(ids);
        if(setmealOnSaleCount > 0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        setmealDishMapper.deleteBySetmealIds(ids);
        setmealMapper.deleteSetmeal(ids);
    }

    /**
     * 起售、停售套餐
     * @param status
     * @param id
     */
    @Override
    public void enableAndDisableSetmeal(Integer status, Long id) {

    }
}
