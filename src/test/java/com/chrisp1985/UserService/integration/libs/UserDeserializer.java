package com.chrisp1985.UserService.integration.libs;

import com.chrisp1985.UserService.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UserDeserializer implements Deserializer<User> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Not needed for this example
    }

    @Override
    public User deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            // Deserialize the byte array into a User object
            return objectMapper.readValue(data, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize User object", e);
        }
    }

    @Override
    public void close() {
        // Not needed for this example
    }
}
