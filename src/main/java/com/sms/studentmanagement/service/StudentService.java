package com.sms.studentmanagement.service;

import com.sms.studentmanagement.dto.StudentDTO;
import com.sms.studentmanagement.entity.Student;
import com.sms.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // ========== CREATE ==========
    public StudentDTO.Response createStudent(
            StudentDTO.Request request) {

        if (studentRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                "Student with email '"
                + request.getEmail()
                + "' already exists");
        }
        Student student = mapToEntity(request);
        return mapToResponse(
            studentRepository.save(student));
    }

    // ========== GET BY ID ==========
    @Transactional(readOnly = true)
    public StudentDTO.Response getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Student not found with id: " + id));
    }

    // ========== GET ALL ==========
    @Transactional(readOnly = true)
    public Page<StudentDTO.Response> getAllStudents(
            int page, int size,
            String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
            PageRequest.of(page, size, sort);

        return studentRepository
            .findAll(pageable)
            .map(this::mapToResponse);
    }

    // ========== SEARCH ==========
    @Transactional(readOnly = true)
    public Page<StudentDTO.Response> searchStudents(
            String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return studentRepository
            .searchStudents(keyword, pageable)
            .map(this::mapToResponse);
    }

    // ========== GET BY DEPARTMENT ==========
    @Transactional(readOnly = true)
    public List<StudentDTO.Response> getStudentsByDepartment(
            String department) {
        return studentRepository
            .findByDepartment(department)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== GET BY COURSE ==========
    @Transactional(readOnly = true)
    public List<StudentDTO.Response> getStudentsByCourse(
            String course) {
        return studentRepository
            .findByCourse(course)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== UPDATE ==========
    public StudentDTO.Response updateStudent(
            Long id, StudentDTO.Request request) {

        Student student = studentRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException(
                    "Student not found with id: " + id));

        // Check email uniqueness
        if (!student.getEmail()
                .equals(request.getEmail()) &&
            studentRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                "Email '"
                + request.getEmail()
                + "' is already in use");
        }

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setCourse(request.getCourse());
        student.setDepartment(request.getDepartment());
        student.setEnrollmentYear(
            request.getEnrollmentYear());
        student.setCgpa(request.getCgpa());

        if (request.getStatus() != null) {
            student.setStatus(request.getStatus());
        }

        return mapToResponse(
            studentRepository.save(student));
    }

    // ========== DELETE ==========
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException(
                "Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // ========== STATISTICS ==========
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalStudents",
            studentRepository.count());
        stats.put("activeStudents",
            studentRepository.countActiveStudents());

        List<Object[]> deptCounts =
            studentRepository.countStudentsByDepartment();

        Map<String, Long> byDept = new HashMap<>();
        for (Object[] row : deptCounts) {
            byDept.put((String) row[0], (Long) row[1]);
        }
        stats.put("studentsByDepartment", byDept);

        return stats;
    }

    // ========== MAPPER: Request → Entity ==========
    private Student mapToEntity(StudentDTO.Request req) {
        return Student.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .dateOfBirth(req.getDateOfBirth())
                .course(req.getCourse())
                .department(req.getDepartment())
                .enrollmentYear(req.getEnrollmentYear())
                .cgpa(req.getCgpa())
                .status(req.getStatus() != null
                    ? req.getStatus()
                    : Student.StudentStatus.ACTIVE)
                .build();
    }

    // ========== MAPPER: Entity → Response ==========
    private StudentDTO.Response mapToResponse(Student s) {
        return StudentDTO.Response.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .phoneNumber(s.getPhoneNumber())
                .dateOfBirth(s.getDateOfBirth())
                .course(s.getCourse())
                .department(s.getDepartment())
                .enrollmentYear(s.getEnrollmentYear())
                .cgpa(s.getCgpa())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}