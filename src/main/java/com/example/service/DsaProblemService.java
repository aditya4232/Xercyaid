package com.example.service;

import com.example.model.DsaProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DsaProblemService {

    private static final List<DsaProblem> problems = new ArrayList<>();

    static {
        // Initialize with some sample problems
        problems.add(new DsaProblem(
                UUID.randomUUID().toString(),
                "Two Sum",
                "Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.",
                "nums = [2, 7, 11, 15], target = 9",
                "[0, 1]"
        ));
        problems.add(new DsaProblem(
                UUID.randomUUID().toString(),
                "Reverse a String",
                "Write a function that reverses a string. The input string is given as an array of characters `s`.",
                "s = [\"h\",\"e\",\"l\",\"l\",\"o\"]",
                "[\"o\",\"l\",\"l\",\"e\",\"h\"]"
        ));
        problems.add(new DsaProblem(
                UUID.randomUUID().toString(),
                "FizzBuzz",
                "Write a program that prints numbers from 1 to n. For multiples of three, print \"Fizz\" instead of the number, and for the multiples of five, print \"Buzz\". For numbers which are multiples of both three and five, print \"FizzBuzz\".",
                "n = 15",
                "1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz"
        ));
    }

    public List<DsaProblem> getAllProblems() {
        return Collections.unmodifiableList(problems);
    }

    public Optional<DsaProblem> getProblemById(String id) {
        return problems.stream()
                       .filter(problem -> problem.getId().equals(id))
                       .findFirst();
    }

    public Optional<DsaProblem> getDailyProblem() {
        // For simplicity, return the first problem.
        // In a real app, this could be random, or based on the date.
        if (!problems.isEmpty()) {
            return Optional.of(problems.get(0));
        }
        return Optional.empty();
    }

    // Future methods: addProblem, updateProblem, deleteProblem
}
