package com.example.service;

import com.example.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EventService {

    private static final Map<String, Event> events = new ConcurrentHashMap<>();
    // SimpleDateFormat is not thread-safe for parsing if not handled carefully.
    // For creation, it's okay if used per call. For formatting, JSTL fmt tags are better in JSPs.
    public static final String DATETIME_LOCAL_FORMAT = "yyyy-MM-dd'T'HH:mm";


    static {
        // Initialize with some sample events for demonstration
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_LOCAL_FORMAT);
            createInitialEvent("Team Meeting", "Weekly team sync-up.", sdf.parse("2024-03-25T10:00"), "Conference Room A", "AdminUser", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5)));
            createInitialEvent("Project Deadline", "Final submission for Project Alpha.", sdf.parse("2024-04-15T23:59"), "Online", "ProjectManager", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)));
            createInitialEvent("Community Tech Talk", "Guest speaker on Microservices.", sdf.parse("2024-03-28T18:30"), "Online / Auditorium", "TechLead", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
            createInitialEvent("Future Webinar", "Exploring new features in Java 21.", sdf.parse("2024-05-10T14:00"), "Online", "JavaExpert", new Date());
        } catch (ParseException e) {
            System.err.println("Error parsing initial event dates: " + e.getMessage());
            // Handle error appropriately, maybe throw a runtime exception or log more severely
        }
    }

    private static void createInitialEvent(String title, String description, Date eventDate, String location, String username, Date dateCreated) {
        String id = UUID.randomUUID().toString();
        Event event = new Event(id, title, description, eventDate, location, username, dateCreated);
        events.put(event.getId(), event);
    }

    public Event createEvent(String title, String description, Date eventDate, String location, String username) {
        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            eventDate == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Title, description, event date, and creator username are required.");
        }
        Event event = new Event(title, description, eventDate, location, username);
        events.put(event.getId(), event);
        return event;
    }

    public List<Event> getAllEvents() {
        List<Event> sortedEvents = new ArrayList<>(events.values());
        // Sort by event date, upcoming first, then by creation date for same event dates
        sortedEvents.sort(Comparator.comparing(Event::getEventDate).thenComparing(Event::getDateCreated, Comparator.reverseOrder()));
        return Collections.unmodifiableList(sortedEvents);
    }

    public Optional<Event> getEventById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    public List<Event> getUpcomingEvents() {
        Date now = new Date();
        return events.values().stream()
                     .filter(event -> event.getEventDate().after(now))
                     .sorted(Comparator.comparing(Event::getEventDate))
                     .collect(Collectors.toList());
    }
    
    public List<Event> getPastEvents() {
        Date now = new Date();
        return events.values().stream()
                     .filter(event -> event.getEventDate().before(now))
                     .sorted(Comparator.comparing(Event::getEventDate).reversed()) // Show most recent past first
                     .collect(Collectors.toList());
    }
}
