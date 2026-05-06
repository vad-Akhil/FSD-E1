package com.campus.event_management.controller;

import com.campus.event_management.entity.Event;
import com.campus.event_management.entity.Student;
import com.campus.event_management.service.EventService;
import com.campus.event_management.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class StudentController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping({"/", "/events"})
    public String browseEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "home";
    }

    @GetMapping("/events/{id}/register")
    public String showRegistrationForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        model.addAttribute("student", new Student());
        return "register";
    }

    @PostMapping("/events/{id}/register")
    public String processRegistration(@PathVariable Long id,
                                      @Valid @ModelAttribute("student") Student student,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            return "register";
        }

        try {
            registrationService.registerStudentForEvent(id, student);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/events";
    }
}
