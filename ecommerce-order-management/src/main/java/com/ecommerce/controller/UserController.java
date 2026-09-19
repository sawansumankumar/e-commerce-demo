package com.ecommerce.controller;

import com.ecommerce.dto.request.UpdateUserRequest;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile()
    {
        UserResponse response = userService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UpdateUserRequest request)
    {
        UserResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public  ResponseEntity<Void> deactivateMyAccount()
    {
        userService.deactivateMyAccount();

        return ResponseEntity.noContent().build();

    }

}
