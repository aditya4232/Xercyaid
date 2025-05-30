package com.example.service;

import com.example.model.Poll;
import com.example.model.PollOption;

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

public class PollService {

    private static final Map<String, Poll> polls = new ConcurrentHashMap<>();

    static {
        // Initialize with some sample polls for demonstration
        List<String> options1Texts = new ArrayList<>();
        options1Texts.add("Java");
        options1Texts.add("Python");
        options1Texts.add("JavaScript");
        options1Texts.add("C#");
        createInitialPoll("What is your favorite programming language for web backend?", options1Texts, "AdminUser", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)));

        List<String> options2Texts = new ArrayList<>();
        options2Texts.add("Spring Boot");
        options2Texts.add("Django");
        options2Texts.add("Express.js");
        options2Texts.add("ASP.NET Core");
        createInitialPoll("Which framework do you prefer for building REST APIs?", options2Texts, "TechEnthusiast", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
    }

    private static void createInitialPoll(String question, List<String> optionTexts, String username, Date dateCreated) {
        List<PollOption> options = new ArrayList<>();
        for (String text : optionTexts) {
            options.add(new PollOption(text));
        }
        String id = UUID.randomUUID().toString();
        Poll poll = new Poll(id, question, options, username, dateCreated);
        polls.put(poll.getId(), poll);
    }

    public Poll createPoll(String question, List<String> optionTexts, String username) {
        if (question == null || question.trim().isEmpty() ||
            optionTexts == null || optionTexts.size() < 2) { // At least 2 options
            throw new IllegalArgumentException("Question and at least two options are required.");
        }

        List<PollOption> options = optionTexts.stream()
                                            .filter(text -> text != null && !text.trim().isEmpty())
                                            .map(PollOption::new)
                                            .collect(Collectors.toList());

        if (options.size() < 2) {
             throw new IllegalArgumentException("At least two valid (non-empty) options are required.");
        }

        Poll poll = new Poll(question, options, username);
        polls.put(poll.getId(), poll);
        return poll;
    }

    public List<Poll> getAllPolls() {
        List<Poll> sortedPolls = new ArrayList<>(polls.values());
        // Sort by date created, newest first
        sortedPolls.sort(Comparator.comparing(Poll::getDateCreated).reversed());
        return Collections.unmodifiableList(sortedPolls);
    }

    public Optional<Poll> getPollById(String id) {
        return Optional.ofNullable(polls.get(id));
    }

    public boolean vote(String pollId, String optionId, String username) {
        // Username is passed for potential future enhancements (e.g., preventing double voting)
        // For now, multiple votes are allowed.
        Optional<Poll> pollOpt = getPollById(pollId);
        if (pollOpt.isPresent()) {
            Poll poll = pollOpt.get();
            PollOption selectedOption = poll.getOptionById(optionId);
            if (selectedOption != null) {
                selectedOption.incrementVote();
                // Potentially log the vote with username and timestamp here
                System.out.println("Vote cast by " + username + " for option " + optionId + " in poll " + pollId);
                return true;
            }
        }
        return false;
    }
}
