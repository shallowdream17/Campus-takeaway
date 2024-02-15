package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addCategory(Category categoryDTO);


    Long count(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> queryCategory(String name, Integer type, Integer startPageNum, int pageSize);

    void updateById(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteCategory(Long id);

    List<Category> queryByType(Integer type);
}
