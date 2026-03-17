package com.sms.studentmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.studentmanagement.dto.AuthDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet
    .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Store token between tests
    private static String token;

    // ✅ Test 1 — Login with correct credentials
    @Test
    @Order(1)
    void testLogin_Success() throws Exception {
        AuthDTO.LoginRequest request =
            new AuthDTO.LoginRequest("admin", "admin123");

        MvcResult result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username")
                .value("admin"))
            .andReturn();

        // Extract token for other tests
        String response =
            result.getResponse().getContentAsString();
        token = objectMapper.readTree(response)
            .get("token").asText();

        System.out.println(
            "✅ Test 1 Passed: Login success");
    }

    // ✅ Test 2 — Login with wrong password
    @Test
    @Order(2)
    void testLogin_WrongPassword() throws Exception {
        AuthDTO.LoginRequest request =
            new AuthDTO.LoginRequest(
                "admin", "wrongpassword");

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
            .andExpect(status().isUnauthorized());

        System.out.println(
            "✅ Test 2 Passed: Wrong password rejected");
    }

    // ✅ Test 3 — Access without token → Forbidden
    @Test
    @Order(3)
    void testGetStudents_NoToken() throws Exception {
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isForbidden());

        System.out.println(
            "✅ Test 3 Passed: No token rejected");
    }

    // ✅ Test 4 — Register new user
    @Test
    @Order(4)
    void testRegister_Success() throws Exception {
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "testuser",
                        "email": "test@example.com",
                        "password": "test123"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message")
                .value("User registered successfully!"));

        System.out.println(
            "✅ Test 4 Passed: Register success");
    }

    // ✅ Test 5 — Create student with valid token
    @Test
    @Order(5)
    void testCreateStudent_Success() throws Exception {
        // First login to get token
        AuthDTO.LoginRequest loginRequest =
            new AuthDTO.LoginRequest("admin", "admin123");

        MvcResult loginResult = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(loginRequest)))
            .andReturn();

        String loginResponse =
            loginResult.getResponse().getContentAsString();
        String freshToken = objectMapper
            .readTree(loginResponse)
            .get("token").asText();

        // Now create student
        mockMvc.perform(
            post("/api/students")
                .header("Authorization",
                    "Bearer " + freshToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Test",
                        "lastName": "Student",
                        "email": "test.student@example.com",
                        "phoneNumber": "9876543210",
                        "dateOfBirth": "2002-01-01",
                        "course": "B.Tech CSE",
                        "department": "Computer Science",
                        "enrollmentYear": 2022,
                        "cgpa": 8.5,
                        "status": "ACTIVE"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName")
                .value("Test"))
            .andExpect(jsonPath("$.status")
                .value("ACTIVE"));

        System.out.println(
            "✅ Test 5 Passed: Create student success");
    }
}