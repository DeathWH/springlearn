package com.wang.springlearn.Mapper;

import com.wang.springlearn.Entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT name FROM tbl_role where id = #{id}")
    List<Role> findNameById(int id);
}
