package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String city;
    private String mobile;
    private LocalDate dob;
}
