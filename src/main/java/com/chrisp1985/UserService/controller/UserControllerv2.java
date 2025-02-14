package com.chrisp1985.UserService.controller;

import com.chrisp1985.UserService.model.UserDto;
import com.chrisp1985.UserService.service.kafka.KafkaProducerService;
import com.chrisp1985.UserService.userdata.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user/v2/kafkaUser")
public class UserControllerv2 {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PostMapping(value = "/add")
    public ResponseEntity<List<UserDto>> addCustomUser(@RequestBody List<User> users) {

        for(User indUser : users) {
            kafkaProducerService.sendKafkaMessage(indUser);
            log.info("Pushed via API: {}", indUser);
        }

        return ResponseEntity.ok(convertAvroUsersToResponseDto(users));
    }

    private List<UserDto> convertAvroUsersToResponseDto(List<User> users) {
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getValue()))
                .collect(Collectors.toList());

    }
}
