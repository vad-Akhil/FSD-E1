package com.smartcampus.eventmanager.service;

import com.smartcampus.eventmanager.model.Event;
import com.smartcampus.eventmanager.model.Registration;
import com.smartcampus.eventmanager.model.User;
import com.smartcampus.eventmanager.repository.EventRepository;
import com.smartcampus.eventmanager.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public Registration registerStudent(Registration registration) {
        Event event = eventRepository.findById(registration.getEvent().getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getCurrentRegistrations() >= event.getCapacity()) {
            throw new RuntimeException("Event is full");
        }

        if (registration.getUser() != null) {
            if (registrationRepository.existsByEventIdAndUserId(event.getId(), registration.getUser().getId())) {
                throw new RuntimeException("You have already registered for this event");
            }
        } else if (registrationRepository.existsByEventIdAndStudentEmail(event.getId(), registration.getStudentEmail())) {
            throw new RuntimeException("Student with this email already registered for this event");
        }

        event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
        eventRepository.save(event);

        return registrationRepository.save(registration);
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
    
    public List<Registration> getRegistrationsByStudent(String email) {
        return registrationRepository.findByStudentEmail(email);
    }

    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUserId(user.getId());
    }
}
