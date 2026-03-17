package com.sms.studentmanagement.dto;

import com.sms.studentmanagement.entity.Student;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotBlank @Email
        private String email;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        @NotBlank
        private String course;
        @NotBlank
        private String department;
        private Integer enrollmentYear;
        @DecimalMin("0.0") @DecimalMax("10.0")
        private Double cgpa;
        private Student.StudentStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private String course;
        private String department;
        private Integer enrollmentYear;
        private Double cgpa;
        private Student.StudentStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}