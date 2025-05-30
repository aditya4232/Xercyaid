package com.example.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Using @WebFilter annotation for filter mapping.
// web.xml update will also be done for completeness and older containers.
// This filter protects /dashboard.jsp and any paths under /api/ (if we add them later)
@WebFilter(urlPatterns = {"/dashboard.jsp", "/api/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
        System.out.println("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Do not create a new session

        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        String requestURI = httpRequest.getRequestURI();
        // Allow access to login, registration pages, and static resources like CSS/JS
        // without authentication.
        // This part is essential to avoid redirect loops or blocking essential resources.
        String contextPath = httpRequest.getContextPath();
        if (isLoggedIn || requestURI.endsWith("/login.html") || requestURI.equals(contextPath + "/login") ||
            requestURI.endsWith("/register.html") || requestURI.equals(contextPath + "/register") ||
            requestURI.startsWith(contextPath + "/css/") || requestURI.startsWith(contextPath + "/js/") || // Allow all /css/* and /js/*
            requestURI.equals(contextPath + "/") || requestURI.endsWith("index.html")) { // Allow access to context root (welcome page)
            chain.doFilter(request, response); // User is logged in or accessing a public page, continue request
        } else {
            System.out.println("User not logged in. Redirecting to login page from URI: " + requestURI);
            httpResponse.sendRedirect(contextPath + "/login.html"); // User not logged in, redirect to login
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
        System.out.println("AuthenticationFilter destroyed");
    }
}
