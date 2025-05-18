package com.meli.notifier.forecast.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
public class UsersController {

    @PostMapping("/subscribe")
    public String subscribe() {
        return "Subscribed";
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe() {
        return "Unsubscribed";
    }



}
