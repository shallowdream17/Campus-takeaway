package com.sky.controller.user;


import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@Slf4j
@Api(tags = "C端-套餐相关接口")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("C端-根据分类id查询套餐")
    public Result queryByCategoryId(Long categoryId){
        log.info("C端-根据分类id查询套餐{}",categoryId);

        List<Setmeal> setmealList = setmealService.queryByCategoryIdInC(categoryId);

        return Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("C端-根据套餐id查询包含的菜品")
    public Result queryDishBySetmealId(@PathVariable Long id){
        log.info("C端-根据套餐id查询包含的菜品");
        List<DishItemVO> dishItemVOList = dishService.queryDishBySetmealId(id);
        return Result.success(dishItemVOList);
    }

}
