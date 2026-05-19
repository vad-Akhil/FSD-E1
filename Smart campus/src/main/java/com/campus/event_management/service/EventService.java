package com.campus.event_management.service;

import com.campus.event_management.entity.Event;
import com.campus.event_management.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> searchEvents(String department, String eventType, LocalDate startDate) {
        return eventRepository.searchEvents(
            (department != null && !department.isBlank()) ? department : null,
            (eventType != null && !eventType.isBlank()) ? eventType : null,
            startDate
        );
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setEventDate(updatedEvent.getEventDate());
            event.setDepartment(updatedEvent.getDepartment());
            event.setEventType(updatedEvent.getEventType());
            event.setMaxCapacity(updatedEvent.getMaxCapacity());
            return eventRepository.save(event);
        }).orElseThrow(() -> new IllegalArgumentException("Event not found with id " + id));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
