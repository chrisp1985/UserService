package com.chrisp1985.UserService.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class UserSerializer implements Serializer<User> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, User user) {
        if (user == null) {
            return null;
        }

        try {
            // Deserialize the byte array into a User object
            return objectMapper.writeValueAsBytes(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize User object", e);
        }
    }
}
