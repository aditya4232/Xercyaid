package com.example.servlet;

import com.example.model.DsaProblem;
import com.example.service.DsaProblemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = {"/dsa/problem", "/dsa/today", "/dsa/submitSolution"})
public class DsaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DsaProblemService dsaProblemService;

    @Override
    public void init() throws ServletException {
        super.init();
        dsaProblemService = new DsaProblemService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        Optional<DsaProblem> problemOpt = Optional.empty();

        if (requestURI.endsWith("/dsa/today")) {
            problemOpt = dsaProblemService.getDailyProblem();
        } else if (requestURI.endsWith("/dsa/problem")) {
            String problemId = request.getParameter("id");
            if (problemId != null && !problemId.isEmpty()) {
                problemOpt = dsaProblemService.getProblemById(problemId);
            } else {
                // Default to daily problem if no ID is provided
                problemOpt = dsaProblemService.getDailyProblem();
            }
        }

        if (problemOpt.isPresent()) {
            request.setAttribute("dsaProblem", problemOpt.get());
            // Forward to a JSP page to display the problem
            request.getRequestDispatcher("/dsa_problem.jsp").forward(request, response);
        } else {
            // Handle problem not found - e.g., show an error page or redirect
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "DSA Problem not found.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getRequestURI().endsWith("/dsa/submitSolution")) {
            String problemId = request.getParameter("problemId");
            String solution = request.getParameter("solution");

            // Log the submission (actual evaluation is out of scope)
            System.out.println("Solution submitted for problem ID: " + problemId);
            System.out.println("Solution: \n" + solution);

            // Set a success message and redirect back to the problem page (or dashboard)
            // For simplicity, redirecting to dashboard with a query parameter message
            // In a real app, you'd likely redirect to the problem page itself.
            request.getSession().setAttribute("dsaMessage", "Solution submitted successfully for problem ID: " + problemId + "!");
            
            // Redirect to the specific problem page if ID is available
            if (problemId != null && !problemId.isEmpty()) {
                 response.sendRedirect(request.getContextPath() + "/dsa/problem?id=" + problemId);
            } else {
                // Fallback to daily problem page or dashboard if ID is missing
                response.sendRedirect(request.getContextPath() + "/dsa/today");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid POST request to DsaServlet.");
        }
    }
}
