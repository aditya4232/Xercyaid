package com.example.model;

import java.util.Date;
import java.util.UUID;

public class Event {
    private String id;
    private String title;
    private String description;
    private Date eventDate;
    private String location; // Optional
    private String createdBy; // Username of the creator
    private Date dateCreated; // When the event record was created

    public Event(String title, String description, Date eventDate, String location, String createdBy) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.createdBy = createdBy;
        this.dateCreated = new Date(); // Set current date/time on creation
    }

    public Event(String id, String title, String description, Date eventDate, String location, String createdBy, Date dateCreated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.createdBy = createdBy;
        this.dateCreated = dateCreated;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Event{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", eventDate=" + eventDate +
               ", createdBy='" + createdBy + '\'' +
               '}';
    }
}
