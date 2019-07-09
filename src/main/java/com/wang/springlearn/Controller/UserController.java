package com.wang.springlearn.Controller;

import com.wang.springlearn.Entity.User;
import com.wang.springlearn.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller()
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
