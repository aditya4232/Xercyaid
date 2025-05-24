package com.example.model;

import java.util.UUID;

public class PollOption {
    private String id;
    private String text;
    private int votes;

    public PollOption(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.votes = 0;
    }

    public PollOption(String id, String text) {
        this.id = id;
        this.text = text;
        this.votes = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getVotes() {
        return votes;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    // Method to increment vote
    public void incrementVote() {
        this.votes++;
    }

    @Override
    public String toString() {
        return "PollOption{" +
               "id='" + id + '\'' +
               ", text='" + text + '\'' +
               ", votes=" + votes +
               '}';
    }
}
