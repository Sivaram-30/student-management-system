package com.sms.studentmanagement.repository;

import com.sms.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository
    extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    List<Student> findByDepartment(String department);

    List<Student> findByCourse(String course);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(s.lastName)  LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(s.email)     LIKE LOWER(CONCAT('%',:kw,'%'))")
    Page<Student> searchStudents(
        @Param("kw") String keyword,
        Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Student s " +
           "WHERE s.status = 'ACTIVE'")
    Long countActiveStudents();

    @Query("SELECT s.department, COUNT(s) " +
           "FROM Student s GROUP BY s.department")
    List<Object[]> countStudentsByDepartment();
}
