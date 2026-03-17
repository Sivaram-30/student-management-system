package com.sms.studentmanagement.service;

import com.sms.studentmanagement.dto.AuthDTO;
import com.sms.studentmanagement.entity.User;
import com.sms.studentmanagement.repository.UserRepository;
import com.sms.studentmanagement.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication
    .AuthenticationManager;
import org.springframework.security.authentication
    .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context
    .SecurityContextHolder;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // ========== LOGIN ==========
    public AuthDTO.LoginResponse login(
            AuthDTO.LoginRequest request) {

        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()));

        SecurityContextHolder
            .getContext()
            .setAuthentication(authentication);

        String jwt =
            jwtUtils.generateJwtToken(authentication);

        org.springframework.security.core
            .userdetails.User userDetails =
            (org.springframework.security.core
                .userdetails.User)
                authentication.getPrincipal();

        Set<String> roles = userDetails
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        User user = userRepository
            .findByUsername(userDetails.getUsername())
            .orElseThrow();

        return AuthDTO.LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(userDetails.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    // ========== REGISTER ==========
    public AuthDTO.MessageResponse register(
            AuthDTO.RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {
            throw new RuntimeException(
                "Username is already taken");
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                "Email is already in use");
        }

        Set<User.Role> roles =
            (request.getRoles() == null ||
             request.getRoles().isEmpty())
                ? Set.of(User.Role.ROLE_STUDENT)
                : request.getRoles();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(
                    request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        return new AuthDTO.MessageResponse(
            "User registered successfully!");
    }
}
