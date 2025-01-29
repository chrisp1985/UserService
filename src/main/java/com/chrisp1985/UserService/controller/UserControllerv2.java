package com.chrisp1985.UserService.controller;

import com.chrisp1985.UserService.service.kafka.KafkaProducerService;
import com.chrisp1985.UserService.userdata.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/v2/kafkaUser")
public class UserControllerv2 {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PostMapping(value = "/add", produces = "application/json")
    public void addCustomUser(@RequestBody List<User> user) {

        for(com.chrisp1985.UserService.userdata.User indUser : user) {
            kafkaProducerService.sendKafkaMessage(indUser);
            log.info("Pushed via API: {}", indUser);
        }

    }
}
