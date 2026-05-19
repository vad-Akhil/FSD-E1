package com.campus.event_management.repository;

import com.campus.event_management.entity.Event;
import com.campus.event_management.entity.Registration;
import com.campus.event_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByStudent(Student student);
    
    long countByEvent(Event event);
    
    boolean existsByEventAndStudent(Event event, Student student);
    
    // Aggregate function to get registration count per event
    @Query("SELECT r.event.title as eventTitle, COUNT(r) as count FROM Registration r GROUP BY r.event.id")
    List<Map<String, Object>> getRegistrationStatistics();
}
