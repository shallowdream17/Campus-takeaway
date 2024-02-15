package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping()
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result queryCategory(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult = categoryService.queryCategory(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result enableAndDisableCategory(@PathVariable Integer status,Long id){
        log.info("修改员工账号状态");
        categoryService.enableAndDisableCategory(status,id);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("删除分类")
    public Result deleteCategory(Long id){
        log.info("删除分类{}",id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @PutMapping()
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类{}",categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result queryByType(Integer type){
        log.info("查询{}分类",type);
        List<Category> list = categoryService.queryByType(type);
        return Result.success(list);
    }

 }
