package com.itasset.management.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class frontController {

    @GetMapping("/front")
    public String showFrontPage() {
        return "front"; // loads front.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // loads login.html
    }
}