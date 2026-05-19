package com.campus.event_management.service;

import com.campus.event_management.entity.Event;
import com.campus.event_management.entity.Registration;
import com.campus.event_management.entity.Student;
import com.campus.event_management.repository.EventRepository;
import com.campus.event_management.repository.RegistrationRepository;
import com.campus.event_management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public Registration registerStudentForEvent(Long eventId, Student providedStudent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getEventDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("Event has already occurred");
        }

        long currentRegistrations = registrationRepository.countByEvent(event);
        if (event.getMaxCapacity() != null && currentRegistrations >= event.getMaxCapacity()) {
            throw new IllegalStateException("Event has reached its maximum capacity");
        }

        // Safely check if student exists or create new
        Student student = studentRepository.findByEmail(providedStudent.getEmail()).orElse(null);
        if (student != null) {
            // Update name in case they are registering with a new preferred name
            student.setName(providedStudent.getName());
            student = studentRepository.save(student);
        } else {
            student = studentRepository.save(providedStudent);
        }

        if (registrationRepository.existsByEventAndStudent(event, student)) {
            throw new IllegalStateException("You are already registered for this event!");
        }

        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setStudent(student);

        return registrationRepository.save(registration);
    }

    public List<Event> getRegisteredEventsForStudent(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return List.of();
        }
        return registrationRepository.findByStudent(studentOpt.get())
                .stream()
                .map(Registration::getEvent)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRegistrationStats() {
        return registrationRepository.getRegistrationStatistics();
    }
}
