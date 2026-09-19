package com.ecommerce.service;

import com.ecommerce.dto.request.LoginRequest;
import com.ecommerce.dto.request.RegisterRequest;
import com.ecommerce.dto.response.LoginResponse;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.exception.EmailAlreadyExistsException;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request)
    {
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent() && existingUser.get().isActive())
        {
            throw new EmailAlreadyExistsException("The provided email is already registered");
        }

        User user;
        if(existingUser.isPresent())
        {
            user =existingUser.get();
            user.setActive(true);
        }

        else
        {
            user = new User();
        }
        
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCity(request.getCity());
        user.setMobile(request.getMobile());
        user.setDob(request.getDob());
        user.setRole(Role.CUSTOMER);
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

    public LoginResponse login(LoginRequest request)
    {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        String token = jwtService.generateToken(email);

        return new LoginResponse(token);

    }
}
