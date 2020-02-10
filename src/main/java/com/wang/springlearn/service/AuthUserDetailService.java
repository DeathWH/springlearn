package com.wang.springlearn.service;

import com.wang.springlearn.Entity.User;
import com.wang.springlearn.Entity.UserRole;
import com.wang.springlearn.Mapper.RoleMapper;
import com.wang.springlearn.Mapper.UserMapper;
import com.wang.springlearn.Mapper.UserRoleMapper;
import com.wang.springlearn.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户身份认证服务类
 */
@Service("userDetailsService")
public class AuthUserDetailService implements UserDetailsService {

    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;
    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException{
        UserDetails userDetails = null;
        try {
            User user = userMapper.findUserByUsername(name).get(0);
            if(user != null){
                List<UserRole> urs = userRoleMapper.findByUserId(user.getId());
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                for(UserRole ur:urs) {
                    String roleName = roleMapper.findNameById(ur.getRole_id()).get(0).getName();
                    SimpleGrantedAuthority grant = new SimpleGrantedAuthority(roleName);
                    authorities.add(grant);
                }
                //封装自定义UserDetails类
                userDetails = new MyUserDetails(user,authorities);
            } else{
                throw new UsernameNotFoundException("该用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }
}
