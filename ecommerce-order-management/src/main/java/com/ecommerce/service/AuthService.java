package com.ecommerce.service;

import com.ecommerce.dto.request.RegisterRequest;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.exception.EmailAlreadyExistsException;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request)
    {
        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new EmailAlreadyExistsException("The provided email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCity(request.getCity());
        user.setMobile(request.getMobile());
        user.setDob(request.getDob());

        Role userRole = roleRepository.findByName("Customer")
                .orElseThrow(() ->
                        new RuntimeException("Customer role not found"));
        user.getRoles().add(userRole);
        User saveUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(saveUser.getId());
        response.setName(saveUser.getName());
        response.setEmail(saveUser.getEmail());
        response.setCity(saveUser.getCity());
        response.setMobile(saveUser.getMobile());
        response.setDob(saveUser.getDob());

        return response;



    }
}
