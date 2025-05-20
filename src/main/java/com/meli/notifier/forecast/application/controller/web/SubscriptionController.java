package com.meli.notifier.forecast.application.controller.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
@AllArgsConstructor
public class SubscriptionController {

//    @PostMapping
//    public ResponseEntity<String> subscribe(@Header("x-auth-token") String authHeader,
//                                            @RequestBody SubscriptionRequestDTO requestBody) {
//
//        return ResponseEntity.ok(requestBody);
//    }

}
