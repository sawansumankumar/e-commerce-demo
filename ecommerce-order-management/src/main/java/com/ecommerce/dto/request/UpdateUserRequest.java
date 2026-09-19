package com.ecommerce.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class UpdateUserRequest {
    private String name;
    private String city;
    private String mobile;
    private LocalDate dob;
}
