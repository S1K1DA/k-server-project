package com.example.k_server_project.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/payment-test")
    public String paymentTest() {
        return "payment-test";
    }
}
