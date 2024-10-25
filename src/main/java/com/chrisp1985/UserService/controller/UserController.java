package com.chrisp1985.UserService.controller;

import com.chrisp1985.UserService.dto.User;
import com.chrisp1985.UserService.sevice.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/users/v1/kafkaUser")
public class UserController {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PostMapping(produces = "application/json")
    public void addCustomUser(@RequestBody User user) throws IOException {

        kafkaProducerService.sendKafkaMessage(user);
        log.info("Pushed via API: {}", user);

    }
}
