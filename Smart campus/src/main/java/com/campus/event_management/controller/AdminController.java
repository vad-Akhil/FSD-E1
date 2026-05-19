package com.campus.event_management.controller;

import com.campus.event_management.entity.Event;
import com.campus.event_management.service.EventService;
import com.campus.event_management.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, 
                            @RequestParam(required = false) String department,
                            @RequestParam(required = false) String eventType,
                            @RequestParam(required = false) LocalDate startDate) {
        model.addAttribute("events", eventService.searchEvents(department, eventType, startDate));
        model.addAttribute("stats", registrationService.getRegistrationStats());
        // For search form pre-fill
        model.addAttribute("department", department);
        model.addAttribute("eventType", eventType);
        model.addAttribute("startDate", startDate);
        return "admin-dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "event-form";
    }

    @PostMapping("/events/new")
    public String saveEvent(@Valid @ModelAttribute("event") Event event, BindingResult result) {
        if (result.hasErrors()) {
            return "event-form";
        }
        eventService.createEvent(event);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/{id}/edit")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        return "event-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable Long id, @Valid @ModelAttribute("event") Event event, BindingResult result) {
        if (result.hasErrors()) {
            return "event-form";
        }
        eventService.updateEvent(id, event);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/admin/dashboard";
    }
}
