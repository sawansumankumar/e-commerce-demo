package com.ecommerce.dto.response;
import com.ecommerce.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String city;
    private String mobile;
    private LocalDate dob;
    private Role role;
    private boolean active;
}
