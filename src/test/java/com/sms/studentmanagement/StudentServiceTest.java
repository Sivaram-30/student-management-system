package com.sms.studentmanagement;

import com.sms.studentmanagement.dto.StudentDTO;
import com.sms.studentmanagement.entity.Student;
import com.sms.studentmanagement.repository.StudentRepository;
import com.sms.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    // Mock means fake database
    @Mock
    private StudentRepository studentRepository;

    // InjectMocks means inject fake repo into service
    @InjectMocks
    private StudentService studentService;

    // ✅ Test 1 — Get student by ID successfully
    @Test
    void testGetStudentById_Success() {
        // ARRANGE — prepare fake data
        Student student = Student.builder()
            .id(1L)
            .firstName("Alice")
            .lastName("Johnson")
            .email("alice@example.com")
            .course("B.Tech CSE")
            .department("Computer Science")
            .status(Student.StudentStatus.ACTIVE)
            .build();

        // Tell mock: when findById(1) is called
        // return our fake student
        when(studentRepository.findById(1L))
            .thenReturn(Optional.of(student));

        // ACT — call the actual method
        StudentDTO.Response response =
            studentService.getStudentById(1L);

        // ASSERT — check the result
        assertNotNull(response);
        assertEquals("Alice", response.getFirstName());
        assertEquals("Johnson", response.getLastName());
        assertEquals("alice@example.com",
            response.getEmail());
        System.out.println(
            "✅ Test 1 Passed: Get student by ID");
    }

    // ✅ Test 2 — Get student by ID not found
    @Test
    void testGetStudentById_NotFound() {
        // Tell mock: return empty when findById called
        when(studentRepository.findById(99L))
            .thenReturn(Optional.empty());

        // Should throw RuntimeException
        RuntimeException exception =
            assertThrows(RuntimeException.class, () ->
                studentService.getStudentById(99L));

        assertEquals(
            "Student not found with id: 99",
            exception.getMessage());
        System.out.println(
            "✅ Test 2 Passed: Student not found");
    }

    // ✅ Test 3 — Create student with duplicate email
    @Test
    void testCreateStudent_DuplicateEmail() {
        StudentDTO.Request request =
            new StudentDTO.Request();
        request.setFirstName("Bob");
        request.setLastName("Smith");
        request.setEmail("alice@example.com");
        request.setCourse("B.Tech ECE");
        request.setDepartment("Electronics");

        // Tell mock: email already exists
        when(studentRepository.existsByEmail(
            "alice@example.com")).thenReturn(true);

        // Should throw exception
        RuntimeException exception =
            assertThrows(RuntimeException.class, () ->
                studentService.createStudent(request));

        assertTrue(exception.getMessage()
            .contains("already exists"));
        System.out.println(
            "✅ Test 3 Passed: Duplicate email check");
    }

    // ✅ Test 4 — Delete student successfully
    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(1L))
            .thenReturn(true);
        doNothing().when(studentRepository)
            .deleteById(1L);

        // Should not throw any exception
        assertDoesNotThrow(() ->
            studentService.deleteStudent(1L));
        System.out.println(
            "✅ Test 4 Passed: Delete student");
    }

    // ✅ Test 5 — Delete student not found
    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(99L))
            .thenReturn(false);

        RuntimeException exception =
            assertThrows(RuntimeException.class, () ->
                studentService.deleteStudent(99L));

        assertEquals(
            "Student not found with id: 99",
            exception.getMessage());
        System.out.println(
            "✅ Test 5 Passed: Delete not found");
    }
}
