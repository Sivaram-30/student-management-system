package com.sms.studentmanagement.controller;

import com.sms.studentmanagement.dto.StudentDTO;
import com.sms.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost
    .PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentController {

    @Autowired
    private StudentService studentService;

    // ---- GET ALL ----
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<Page<StudentDTO.Response>>
            getAllStudents(
                @RequestParam(defaultValue = "0")
                    int page,
                @RequestParam(defaultValue = "10")
                    int size,
                @RequestParam(defaultValue = "id")
                    String sortBy,
                @RequestParam(defaultValue = "asc")
                    String direction) {
        return ResponseEntity.ok(
            studentService.getAllStudents(
                page, size, sortBy, direction));
    }

    // ---- SEARCH ----
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<Page<StudentDTO.Response>>
            searchStudents(
                @RequestParam String keyword,
                @RequestParam(defaultValue = "0")
                    int page,
                @RequestParam(defaultValue = "10")
                    int size) {
        return ResponseEntity.ok(
            studentService.searchStudents(
                keyword, page, size));
    }

    // ---- GET BY ID ----
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<StudentDTO.Response>
            getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(
            studentService.getStudentById(id));
    }

    // ---- GET BY DEPARTMENT ----
    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<StudentDTO.Response>>
            getByDepartment(
                @PathVariable String department) {
        return ResponseEntity.ok(
            studentService
                .getStudentsByDepartment(department));
    }

    // ---- GET BY COURSE ----
    @GetMapping("/course/{course}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<StudentDTO.Response>>
            getByCourse(@PathVariable String course) {
        return ResponseEntity.ok(
            studentService.getStudentsByCourse(course));
    }

    // ---- CREATE ----
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO.Response>
            createStudent(
                @Valid @RequestBody
                    StudentDTO.Request request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(studentService.createStudent(request));
    }

    // ---- UPDATE ----
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO.Response>
            updateStudent(
                @PathVariable Long id,
                @Valid @RequestBody
                    StudentDTO.Request request) {
        return ResponseEntity.ok(
            studentService.updateStudent(id, request));
    }

    // ---- DELETE ----
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>>
            deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(
            Map.of("message",
                "Student deleted successfully"));
    }

    // ---- STATISTICS ----
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>>
            getStatistics() {
        return ResponseEntity.ok(
            studentService.getStatistics());
    }
}
