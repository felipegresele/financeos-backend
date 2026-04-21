package com.financeos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class StatusController {

    @GetMapping("/status")
    public String status() {
        return "FinanceOS API online";
    }
}
