package com.smartcampus.eventmanager.config;

import com.smartcampus.eventmanager.model.Event;
import com.smartcampus.eventmanager.model.EventType;
import com.smartcampus.eventmanager.model.User;
import com.smartcampus.eventmanager.repository.EventRepository;
import com.smartcampus.eventmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedEvents();
        updateExistingEventCoordinates(); // Update existing DB records if they are still set to New York
    }

    private void updateExistingEventCoordinates() {
        java.util.List<Event> events = eventRepository.findAll();
        boolean updated = false;
        for (Event e : events) {
            // If the event has NO coordinates, or is near New York (old seed data), move it to Campus
            if (e.getLatitude() == null || e.getLatitude() > 40.0) {
                // Generate a random spot on Stanford Campus near 37.4275, -122.1697
                double lat = 37.4275 + (Math.random() - 0.5) * 0.005;
                double lng = -122.1697 + (Math.random() - 0.5) * 0.005;
                e.setLatitude(lat);
                e.setLongitude(lng);
                updated = true;
            }
            
            // Force browser to bypass image cache by appending a version string
            if (e.getImageUrl() != null && !e.getImageUrl().contains("?v=")) {
                e.setImageUrl(e.getImageUrl() + "?v=" + System.currentTimeMillis());
                updated = true;
            }
        }
        if (updated) {
            eventRepository.saveAll(events);
            System.out.println("✅ Updated old or missing event coordinates to new College Campus locations and refreshed image cache!");
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Seed Admin
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@smartcampus.com")
                    .roles(new HashSet<>(Arrays.asList("ADMIN", "USER")))
                    .build();

            // Seed Test Student
            User student = User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("John Doe")
                    .email("john.doe@university.edu")
                    .studentId("ST12345")
                    .department("Computer Science")
                    .roles(new HashSet<>(Collections.singletonList("USER")))
                    .build();

            userRepository.saveAll(Arrays.asList(admin, student));
            System.out.println("✅ Seeded default users: admin/admin123 and student/student123");
        }
    }

    private void seedEvents() {
        if (eventRepository.count() == 0) {
            Event event1 = Event.builder()
                    .title("Cloud Computing Workshop")
                    .description("Master AWS and Azure fundamentals in this hands-on workshop. Learn to deploy scalable applications, manage cloud infrastructure, and understand key services that power modern tech companies.")
                    .dateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                    .location("Tech Center Lab 4")
                    .department("Computer Science")
                    .eventType(EventType.WORKSHOP)
                    .capacity(40)
                    .imageUrl("/images/cloud.png")
                    .latitude(37.4276)
                    .longitude(-122.1700)
                    .currentRegistrations(0)
                    .build();

            Event event2 = Event.builder()
                    .title("Annual Tech Fest 2026")
                    .description("The biggest technology festival on campus featuring hackathons, coding competitions, robotics showcases, and keynote speeches from industry leaders.")
                    .dateTime(LocalDateTime.now().plusWeeks(1).withHour(9).withMinute(0))
                    .location("University Main Ground")
                    .department("All Departments")
                    .eventType(EventType.FESTIVAL)
                    .capacity(500)
                    .imageUrl("/images/fest.png")
                    .latitude(37.4282)
                    .longitude(-122.1685)
                    .currentRegistrations(0)
                    .build();

            Event event3 = Event.builder()
                    .title("Seminar on AI Ethics")
                    .description("A deep dive into the ethical implications of Artificial Intelligence. Explore bias in ML models, responsible AI deployment, and the future of AI governance.")
                    .dateTime(LocalDateTime.now().plusDays(5).withHour(14).withMinute(30))
                    .location("Seminar Hall B")
                    .department("Philosophy & AI")
                    .eventType(EventType.SEMINAR)
                    .capacity(100)
                    .imageUrl("/images/ai.png")
                    .latitude(37.4268)
                    .longitude(-122.1670)
                    .currentRegistrations(0)
                    .build();

            Event event4 = Event.builder()
                    .title("Robotics Innovation Challenge")
                    .description("Build and program autonomous robots to navigate obstacle courses. Teams of 3-5 students compete for the Innovation Cup and industry internship opportunities.")
                    .dateTime(LocalDateTime.now().plusDays(8).withHour(11).withMinute(0))
                    .location("Engineering Block Lab 2")
                    .department("Mechanical Engineering")
                    .eventType(EventType.WORKSHOP)
                    .capacity(60)
                    .imageUrl("/images/robot.png")
                    .latitude(37.4290)
                    .longitude(-122.1710)
                    .currentRegistrations(0)
                    .build();

            Event event5 = Event.builder()
                    .title("Stock Market & Finance Webinar")
                    .description("Learn market analysis techniques, portfolio management strategies, and get insights from Wall Street professionals on navigating volatile markets.")
                    .dateTime(LocalDateTime.now().plusDays(3).withHour(16).withMinute(0))
                    .location("Virtual - Zoom Link")
                    .department("Management & Finance")
                    .eventType(EventType.WEBINAR)
                    .capacity(200)
                    .imageUrl("/images/stocks.png")
                    .latitude(37.4260)
                    .longitude(-122.1730)
                    .currentRegistrations(0)
                    .build();

            Event event6 = Event.builder()
                    .title("Business Plan Competition")
                    .description("Pitch your startup idea to a panel of venture capitalists and industry mentors. Top 3 teams receive seed funding and incubation support.")
                    .dateTime(LocalDateTime.now().plusDays(10).withHour(9).withMinute(30))
                    .location("Business School Auditorium")
                    .department("Business Administration")
                    .eventType(EventType.OTHER)
                    .capacity(80)
                    .imageUrl("/images/business.png")
                    .latitude(37.4285)
                    .longitude(-122.1660)
                    .currentRegistrations(0)
                    .build();

            eventRepository.saveAll(Arrays.asList(event1, event2, event3, event4, event5, event6));
            System.out.println("✅ Seeded 6 sample events with images");
        }
    }
}
