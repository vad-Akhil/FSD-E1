package com.smartcampus.eventmanager.controller;

import com.smartcampus.eventmanager.model.Event;
import com.smartcampus.eventmanager.model.Registration;
import com.smartcampus.eventmanager.model.User;
import com.smartcampus.eventmanager.service.EventService;
import com.smartcampus.eventmanager.service.RegistrationService;
import com.smartcampus.eventmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredEvents", eventService.getUpcomingEvents());
        model.addAttribute("totalEvents", eventService.getTotalEvents());
        model.addAttribute("totalRegistrations", eventService.getTotalRegistrations());
        return "home";
    }

    @GetMapping("/events")
    public String listEvents(@RequestParam(required = false) String dept, 
                             @RequestParam(required = false) String type, 
                             Model model) {
        if (dept != null && !dept.isEmpty()) {
            model.addAttribute("events", eventService.filterByDepartment(dept));
        } else {
            model.addAttribute("events", eventService.getAllEvents());
        }
        return "events";
    }

    @GetMapping("/map")
    public String showMap(Model model) {
        return "map";
    }

    @GetMapping("/register/{id}")
    public String showRegistrationForm(@PathVariable Long id, 
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        Registration registration = new Registration();
        registration.setEvent(event);

        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            if (user != null) {
                registration.setStudentName(user.getFullName());
                registration.setStudentEmail(user.getEmail());
                registration.setStudentId(user.getStudentId());
                registration.setDepartment(user.getDepartment());
            }
        }
        
        model.addAttribute("registration", registration);
        model.addAttribute("event", event);
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute Registration registration, 
                                      BindingResult result, 
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("event", eventService.getEventById(registration.getEvent().getId()).get());
            return "register";
        }

        Event currentEvent = eventService.getEventById(registration.getEvent().getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        try {
            if (userDetails != null) {
                User user = userService.findByUsername(userDetails.getUsername());
                registration.setUser(user);
            }
            registration.setEvent(currentEvent);
            registrationService.registerStudent(registration);
            redirectAttributes.addFlashAttribute("success", "Successfully registered for " + currentEvent.getTitle());
            return "redirect:/events";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("event", currentEvent);
            return "register";
        }
    }

    @GetMapping("/my-registrations")
    public String showMyRegistrations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("registrations", registrationService.getRegistrationsByUser(user));
        }
        return "my-registrations";
    }
}
