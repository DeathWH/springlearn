package com.wang.springlearn.Mapper;

import com.wang.springlearn.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM tbl_user")
    List<User> getAll();

    @Select("SELECT * FROM tbl_user where username = #{username}")
    List<User> findUserByUsername(String username);
}
