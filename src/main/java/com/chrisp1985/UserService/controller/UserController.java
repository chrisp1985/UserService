package com.chrisp1985.UserService.controller;

import com.chrisp1985.UserService.dto.User;
import com.chrisp1985.UserService.sevice.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/v1/kafkaUser")
public class UserController {

    KafkaProducerService kafkaProducerService;

    @Autowired
    public UserController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<String> addCustomUser(@RequestBody User user) {
        kafkaProducerService.sendKafkaMessage(user);
        log.info("Pushed via API: {}", user);
        return ResponseEntity.ok("User created successfully");
    }
}
