package com.wang.springlearn.Service;

import com.wang.springlearn.Entity.User;
import com.wang.springlearn.Mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public List<User> getAll() {
        return userMapper.getAll();
    }
}
