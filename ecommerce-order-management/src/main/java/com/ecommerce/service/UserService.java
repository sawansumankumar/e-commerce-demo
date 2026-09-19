package com.ecommerce.service;

import com.ecommerce.dto.request.UpdateUserRequest;
import com.ecommerce.dto.response.AdminUserResponse;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService
{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public UserResponse getMyProfile()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->  new UsernameNotFoundException("User not found with email: " + email));
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDob(user.getDob());
        response.setCity(user.getCity());
        response.setMobile(user.getMobile());

        return response;
    }

    public UserResponse updateMyProfile(UpdateUserRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->  new UserNotFoundException("User not found with email: " + email));
        if(request.getMobile() != null)
        {
        user.setMobile(request.getMobile());
        }
        if(request.getCity() != null)
        {
            user.setCity(request.getCity());
        }
        if(request.getName() != null) {
            user.setName(request.getName());
        }
        if(request.getDob() != null) {
            user.setDob(request.getDob());
        }

        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setCity(savedUser.getCity());
        response.setMobile(savedUser.getMobile());
        response.setDob(savedUser.getDob());

        return response;

    }

    public void deactivateMyAccount()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found with email: "+ email));

        user.setActive(false);
        userRepository.save(user);
    }

    public List<AdminUserResponse> getAllUsers()
    {
        List<User> users = userRepository.findAll();
        List<AdminUserResponse> responses = new ArrayList<>();
        for(User user : users)
        {
            AdminUserResponse response = new AdminUserResponse();
            response.setName(user.getName());
            response.setDob(user.getDob());
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setMobile(user.getMobile());
            response.setCity(user.getCity());
            response.setActive(user.isActive());
            response.setRole(user.getRole());
            responses.add(response);
        }
        return responses;
    }

    public AdminUserResponse getUserById(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("There is no User with this id " + id));
        AdminUserResponse response = new AdminUserResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCity(user.getCity());
        response.setDob(user.getDob());
        response.setMobile(user.getMobile());
        response.setId(user.getId());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        return response;
    }

    public void deactivateUserByAdmin(Long userId)
    {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("There is no user with the id: " + userId));

        if(user.getRole() != Role.ADMIN)
        {
            user.setActive(false);
            userRepository.save(user);
        }
        else
        {
            throw new AccessDeniedException("Admin accounts cannot be deactivated");
        }
    }

    public void activateUserByAdmin(Long userId)
    {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("There is no user with the id: " + userId));

        user.setActive(true);
        userRepository.save(user);
    }
}
