package com.example.servlet;

import com.example.model.EducationalPost;
import com.example.service.EducationalPostService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/posts", "/posts/list"})
public class PostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EducationalPostService postService;

    @Override
    public void init() throws ServletException {
        super.init();
        postService = new EducationalPostService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String postId = request.getParameter("id"); // Changed from postId to id to match common practice

        if (postId != null && !postId.isEmpty()) {
            // Request for a single post, e.g., /posts?id=some-id
            Optional<EducationalPost> postOpt = postService.getPostById(postId);
            if (postOpt.isPresent()) {
                request.setAttribute("educationalPost", postOpt.get());
                request.getRequestDispatcher("/single_post.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Educational Post not found.");
            }
        } else if (requestURI.endsWith("/posts/list") || requestURI.endsWith("/posts")) {
            // Request to list all posts, e.g., /posts/list or /posts
            // Defaulting /posts to list view for simplicity
            List<EducationalPost> posts = postService.getAllPosts();
            request.setAttribute("postList", posts);
            request.getRequestDispatcher("/list_posts.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request for posts.");
        }
    }
}
