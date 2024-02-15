package com.sky.service;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    PageResult queryCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    void enableAndDisableCategory(Integer status, Long id);

    void deleteCategory(Long id);

    void updateCategory(CategoryDTO categoryDTO);

    List<Category> queryByType(Integer type);
}
