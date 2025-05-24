package com.example.servlet;

import com.example.model.Poll;
import com.example.service.PollService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/polls")
public class PollServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PollService pollService;

    @Override
    public void init() throws ServletException {
        super.init();
        pollService = new PollService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String pollId = request.getParameter("pollId");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        if ("create".equals(action)) {
            request.getRequestDispatcher("/create_poll.jsp").forward(request, response);
        } else if (pollId != null && !pollId.isEmpty()) {
            Optional<Poll> pollOpt = pollService.getPollById(pollId);
            if (pollOpt.isPresent()) {
                request.setAttribute("poll", pollOpt.get());
                request.getRequestDispatcher("/view_poll.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Poll not found.");
                request.getRequestDispatcher("/list_polls.jsp").forward(request, response);
            }
        } else { // Default: list all polls
            List<Poll> polls = pollService.getAllPolls();
            request.setAttribute("pollList", polls);
            request.getRequestDispatcher("/list_polls.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to perform this action.");
            return;
        }
        String username = (String) session.getAttribute("loggedInUser");

        if ("create".equals(action)) {
            String question = request.getParameter("question");
            // Get options. Assuming options are named option1, option2, option3, option4
            // A more dynamic way would be to use JS to add/remove fields and name them e.g. "options[]"
            List<String> optionTexts = new ArrayList<>();
            if (request.getParameter("option1") != null && !request.getParameter("option1").trim().isEmpty()) optionTexts.add(request.getParameter("option1").trim());
            if (request.getParameter("option2") != null && !request.getParameter("option2").trim().isEmpty()) optionTexts.add(request.getParameter("option2").trim());
            if (request.getParameter("option3") != null && !request.getParameter("option3").trim().isEmpty()) optionTexts.add(request.getParameter("option3").trim());
            if (request.getParameter("option4") != null && !request.getParameter("option4").trim().isEmpty()) optionTexts.add(request.getParameter("option4").trim());
            
            // Alternative: get all parameters starting with "option"
            // List<String> optionTexts = request.getParameterMap().entrySet().stream()
            //     .filter(entry -> entry.getKey().startsWith("option") && entry.getValue() != null && !entry.getValue()[0].trim().isEmpty())
            //     .map(entry -> entry.getValue()[0].trim())
            //     .collect(Collectors.toList());


            try {
                Poll newPoll = pollService.createPoll(question, optionTexts, username);
                session.setAttribute("successMessage", "Poll created successfully!");
                response.sendRedirect(request.getContextPath() + "/polls?pollId=" + newPoll.getId());
            } catch (IllegalArgumentException e) {
                session.setAttribute("errorMessage", e.getMessage());
                // Forward back to create form with error
                // Need to repopulate form fields, which is complex without framework.
                // For simplicity, just redirecting, user will have to re-enter.
                response.sendRedirect(request.getContextPath() + "/polls?action=create");
            }

        } else if ("vote".equals(action)) {
            String pollId = request.getParameter("pollId");
            String optionId = request.getParameter("optionId");

            if (pollId == null || optionId == null) {
                session.setAttribute("errorMessage", "Poll ID and Option ID are required to vote.");
                response.sendRedirect(request.getContextPath() + (pollId != null ? "/polls?pollId=" + pollId : "/polls"));
                return;
            }

            boolean success = pollService.vote(pollId, optionId, username);
            if (success) {
                session.setAttribute("successMessage", "Your vote has been recorded!");
            } else {
                session.setAttribute("errorMessage", "Failed to record your vote. Poll or option not found.");
            }
            response.sendRedirect(request.getContextPath() + "/polls?pollId=" + pollId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }
}
