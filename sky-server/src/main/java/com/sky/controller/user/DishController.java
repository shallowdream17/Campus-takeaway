package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Slf4j
@Api(tags = "C端-菜品接口")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("list")
    @ApiOperation("C端-根据分类id查询菜品")
    public Result queryDish(Long categoryId){
        log.info("C端-根据分类id查询菜品{}",categoryId);
        //先查询是否在redis缓存中
        String key = "dish_"+categoryId;
        List<DishVO> dishVOCache = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //如果在缓存中，直接返回
        if(dishVOCache != null && dishVOCache.size() > 0){
            return Result.success(dishVOCache);
        }

        //如果不在缓存中
        List<DishVO> dishVOList = dishService.queryDishByCategoryIdInC(categoryId);
        redisTemplate.opsForValue().set(key,dishVOList);
        return Result.success(dishVOList);
    }
}
