package com.campus.event_management.repository;

import com.campus.event_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Custom search methods for filters
    List<Event> findByDepartmentContainingIgnoreCase(String department);
    List<Event> findByEventTypeContainingIgnoreCase(String eventType);
    List<Event> findByEventDateAfter(LocalDate date);
    
    @Query("SELECT e FROM Event e WHERE " +
           "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:eventType IS NULL OR LOWER(e.eventType) LIKE LOWER(CONCAT('%', :eventType, '%'))) AND " +
           "(:startDate IS NULL OR e.eventDate >= :startDate)")
    List<Event> searchEvents(@Param("department") String department, 
                             @Param("eventType") String eventType, 
                             @Param("startDate") LocalDate startDate);
}
