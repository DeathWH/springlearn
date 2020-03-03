package com.wang.springlearn.Controller;

import com.wang.springlearn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

//@Controller()
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getAllUsers")
    public String getAllUsers(HashMap map ) {
        List userList = userService.getAll();
        map.put("userList",userList);
        return "userInfo";
    }

}
