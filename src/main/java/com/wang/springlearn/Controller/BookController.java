package com.wang.springlearn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BookController {
    @RequestMapping(value = "/bookList")
    public String getAllBooks(Model model){
        return "booklist";
    }

}
