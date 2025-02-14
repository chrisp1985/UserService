package com.chrisp1985.UserService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    Integer id;
    String name;
    Integer value;
}
