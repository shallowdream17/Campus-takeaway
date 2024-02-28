package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping()
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.addDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result dishPageQuery( DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    public Result dishDelete(Long[] ids){
        log.info("菜品批量删除{}",ids);
        dishService.dishDelete(ids);
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result queryById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVo = dishService.queryById(id);
        return Result.success(dishVo);
    }

    /**
     * 修改菜品
     */
    @PutMapping()
    @ApiOperation("修改菜品")
    public Result updateById(@RequestBody DishDTO dishDTO){
        log.info("修改菜品{}",dishDTO);
        dishService.updateById(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 启售、停售菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("起售、停售菜品")
    public Result enableAndDisableDish(@PathVariable Integer status,Long id){
        log.info("{}号菜品状态设置为{}",id,status);
        dishService.enableAndDisableDish(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result queryByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品{}",categoryId);
        List<Dish> list = dishService.queryByCategoryId(categoryId);
        return Result.success(list);
    }


    /**
     * 缓存删除
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
