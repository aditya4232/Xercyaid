package com.example.model;

import java.util.Date;
import java.util.UUID;

public class EducationalPost {
    private String id;
    private String title;
    private String content; // Can be Markdown or plain text
    private String author;
    private Date datePosted;
    private String type; // e.g., "article", "video_link"
    private String videoUrl; // Optional, used if type is "video_link"

    public EducationalPost(String title, String content, String author, String type, String videoUrl) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.author = author;
        this.datePosted = new Date(); // Set current date/time on creation
        this.type = type;
        this.videoUrl = videoUrl;
    }

    public EducationalPost(String id, String title, String content, String author, Date datePosted, String type, String videoUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.datePosted = datePosted;
        this.type = type;
        this.videoUrl = videoUrl;
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public String getType() {
        return type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "EducationalPost{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", datePosted=" + datePosted +
               ", type='" + type + '\'' +
               '}';
    }
}
