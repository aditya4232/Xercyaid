package com.example.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Poll {
    private String id;
    private String question;
    private List<PollOption> options;
    private String createdBy; // username
    private Date dateCreated;

    public Poll(String question, List<PollOption> options, String createdBy) {
        this.id = UUID.randomUUID().toString();
        this.question = question;
        this.options = options;
        this.createdBy = createdBy;
        this.dateCreated = new Date();
    }

    public Poll(String id, String question, List<PollOption> options, String createdBy, Date dateCreated) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.createdBy = createdBy;
        this.dateCreated = dateCreated;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<PollOption> getOptions() {
        return options;
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    // Helper method to get a specific option by ID
    public PollOption getOptionById(String optionId) {
        if (optionId == null || options == null) {
            return null;
        }
        for (PollOption option : options) {
            if (optionId.equals(option.getId())) {
                return option;
            }
        }
        return null;
    }
    
    // Helper method to calculate total votes
    public int getTotalVotes() {
        int total = 0;
        if (options != null) {
            for (PollOption option : options) {
                total += option.getVotes();
            }
        }
        return total;
    }


    @Override
    public String toString() {
        return "Poll{" +
               "id='" + id + '\'' +
               ", question='" + question + '\'' +
               ", createdBy='" + createdBy + '\'' +
               ", dateCreated=" + dateCreated +
               ", optionsCount=" + (options != null ? options.size() : 0) +
               '}';
    }
}
