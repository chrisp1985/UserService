package com.chrisp1985.UserService.unit;

import com.chrisp1985.UserService.controller.UserController;
import com.chrisp1985.UserService.service.kafka.KafkaProducerService;
import com.chrisp1985.UserService.userdata.User;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    KafkaProducerService kafkaProducerService;

    @Test
    void addCustomUserTest() {
        User testUser = new User(123, "TestUser", 100);

        // Act
        ResponseEntity<String> response = userController.addCustomUser(testUser);

        // Assert
        verify(kafkaProducerService, times(1)).sendKafkaMessage(testUser);
        Assertions.assertEquals("User created successfully", response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void addingNullValuesAddsCustomUserTest() {
        User testUser = new User(0, "", 0);
//
//        // Act
//        mockMvc.perform(post("/user/v1/kafkaUser")
//                        .content(new ObjectMapper().writeValueAsString(invalidUser)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.name").value("Name must be between 2 and 50 characters"))
//                .andExpect(jsonPath("$.email").value("Email should be valid"));
//
//
//        ResponseEntity<String> response = userController.addCustomUser(testUser);
//
//        // Assert
//        verify(kafkaProducerService, times(1)).sendKafkaMessage(testUser);
//        Assertions.assertEquals("User created successfully", response.getBody());
//        Assertions.assertEquals(200, response.getStatusCode().value());
    }
}
