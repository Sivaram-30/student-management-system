package com.sms.studentmanagement.dto;

import com.sms.studentmanagement.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

public class AuthDTO {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 20)
        private String username;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        private Set<User.Role> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginResponse {
        private String token;
        private String type;
        private Long id;
        private String username;
        private String email;
        private Set<String> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}
