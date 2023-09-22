package com.example.seckilldemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String login() {
        return "loginweb";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }


//    @PostConstruct
//    public void init(){
//        String wxq1234561 = new BCryptPasswordEncoder().encode("wxq123456");
//        String wxq1234562 = new BCryptPasswordEncoder().encode("wxq123456");
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        boolean match = passwordEncoder.matches("wxq123456", "$2a$10$m73rtC1Fe1wA4kOzBJ0yfufoTceZKrkJ4yOuTlBvIvYW3FA07NRau");
//        System.out.println(match);
//    }
}
