package com.imooc.mall.dao;

import com.imooc.mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(@Param("username") String username);

    int countByEmail(@Param("email") String email);

    User selectByUsername(@Param("username") String username);
}