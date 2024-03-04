package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 插入用户，同时返回主键值id
     * @param user
     */
    void insert(User user);

    /**
     * 查询用户数量和新增数量
     * @param map
     * @return
     */
    Integer getUserStatistics(Map map);
}
