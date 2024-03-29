package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    @PostMapping()
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache" ,key = "#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐{}",setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.setmealPageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteSetmeal(Long[] ids){
        log.info("删除套餐{}",ids);
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result querySetmealById(@PathVariable Long id){
        log.info("根据id查询套餐{}",id);
        SetmealVO setmealVO = setmealService.querySetmealById(id);
        return Result.success(setmealVO);
    }

    @PutMapping()
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache" ,allEntries = true)
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("起售、停售套餐")
    @CacheEvict(cacheNames = "setmealCache " ,allEntries = true)
    public Result enableAndDisableSetmeal(@PathVariable Integer status,Long id){
        log.info("套餐{}状态设置为{}",id,status);
        setmealService.enableAndDisableSetmeal(status,id);
        return Result.success();
    }


}
