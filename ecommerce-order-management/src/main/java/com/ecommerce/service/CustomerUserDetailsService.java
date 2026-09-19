package com.ecommerce.service;


import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("This email is not registered"));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name());

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).
                password(user.getPasswordHash()).authorities(authority).disabled(!user.isActive()).build();

    }


}
