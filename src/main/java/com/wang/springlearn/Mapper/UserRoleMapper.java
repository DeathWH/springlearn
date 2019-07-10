package com.wang.springlearn.Mapper;

import com.wang.springlearn.Entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    @Select("select * from tbl_user_role where user_id=#{userId}")
    List<UserRole>findByUserId( int userId);
}
