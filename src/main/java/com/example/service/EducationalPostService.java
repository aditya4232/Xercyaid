package com.example.service;

import com.example.model.EducationalPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EducationalPostService {

    private static final List<EducationalPost> posts = new ArrayList<>();

    static {
        // Initialize with some sample posts
        posts.add(new EducationalPost(
                UUID.randomUUID().toString(),
                "Understanding Big O Notation",
                "A comprehensive guide to understanding algorithm efficiency and Big O notation. This article covers common complexities like O(1), O(log n), O(n), O(n log n), O(n^2), and O(2^n) with examples.",
                "Admin User",
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5)), // 5 days ago
                "article",
                null
        ));
        posts.add(new EducationalPost(
                UUID.randomUUID().toString(),
                "Introduction to Serverless with AWS Lambda",
                "This video provides a beginner-friendly introduction to serverless computing concepts and demonstrates how to create your first AWS Lambda function using Node.js.",
                "Tech Guru",
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)), // 3 days ago
                "video_link",
                "https://www.youtube.com/embed/eOBq__h4OJ4" // Example YouTube embed link
        ));
        posts.add(new EducationalPost(
                UUID.randomUUID().toString(),
                "Mastering CSS Grid Layout",
                "Learn the fundamentals of CSS Grid Layout, a powerful tool for creating complex web page layouts with ease. This article includes practical examples and common use cases.",
                "Web Designer Pro",
                new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)), // 1 day ago
                "article",
                null
        ));
         posts.add(new EducationalPost(
                UUID.randomUUID().toString(),
                "Java Concurrency Basics",
                "An overview of threads, synchronization, and common concurrency utilities in Java. Essential for building robust multi-threaded applications.",
                "Java Expert",
                new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)), // 30 minutes ago
                "article",
                null
        ));
    }

    public List<EducationalPost> getAllPosts() {
        // Return a sorted list (newest first)
        List<EducationalPost> sortedPosts = new ArrayList<>(posts);
        sortedPosts.sort(Comparator.comparing(EducationalPost::getDatePosted).reversed());
        return Collections.unmodifiableList(sortedPosts);
    }

    public Optional<EducationalPost> getPostById(String id) {
        return posts.stream()
                    .filter(post -> post.getId().equals(id))
                    .findFirst();
    }

    // Future methods: addPost, updatePost, deletePost
}
