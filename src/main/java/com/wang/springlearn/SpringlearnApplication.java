package com.wang.springlearn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
@MapperScan(basePackages = {"com.wang.springlearn.Mapper"})
//public class SpringlearnApplication extends SpringBootServletInitializer {
public class SpringlearnApplication  {

    public static void main(String[] args) {
        SpringApplication.run(SpringlearnApplication.class, args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(SpringlearnApplication.class);
//    }

}
