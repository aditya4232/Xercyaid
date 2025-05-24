package com.example.servlet;

import com.example.model.Event;
import com.example.service.EventService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@WebServlet("/events")
public class EventServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EventService eventService;

    @Override
    public void init() throws ServletException {
        super.init();
        eventService = new EventService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String eventId = request.getParameter("eventId"); // Changed from eventId to eventId for consistency
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        if ("create".equals(action)) {
            request.getRequestDispatcher("/create_event.jsp").forward(request, response);
        } else if (eventId != null && !eventId.isEmpty()) {
            Optional<Event> eventOpt = eventService.getEventById(eventId);
            if (eventOpt.isPresent()) {
                request.setAttribute("event", eventOpt.get());
                request.getRequestDispatcher("/view_event.jsp").forward(request, response);
            } else {
                session.setAttribute("errorMessage", "Event not found.");
                response.sendRedirect(request.getContextPath() + "/events");
            }
        } else { // Default: list all events
            String view = request.getParameter("view"); // upcoming, past, all
            List<Event> eventList;
            if("upcoming".equals(view)) {
                eventList = eventService.getUpcomingEvents();
                request.setAttribute("eventListView", "Upcoming");
            } else if ("past".equals(view)) {
                eventList = eventService.getPastEvents();
                 request.setAttribute("eventListView", "Past");
            }
            else {
                 eventList = eventService.getAllEvents();
                 request.setAttribute("eventListView", "All");
            }
            request.setAttribute("eventList", eventList);
            request.getRequestDispatcher("/list_events.jsp").forward(request, response);
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
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String eventDateStr = request.getParameter("eventDate"); // Expects yyyy-MM-ddTHH:mm
            String location = request.getParameter("location");

            if (title == null || title.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                eventDateStr == null || eventDateStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Title, description, and event date are required.");
                response.sendRedirect(request.getContextPath() + "/events?action=create");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(EventService.DATETIME_LOCAL_FORMAT);
            Date eventDate;
            try {
                eventDate = sdf.parse(eventDateStr);
            } catch (ParseException e) {
                session.setAttribute("errorMessage", "Invalid event date format. Please use YYYY-MM-DDTHH:MM.");
                response.sendRedirect(request.getContextPath() + "/events?action=create");
                return;
            }

            try {
                Event newEvent = eventService.createEvent(title, description, eventDate, location, username);
                session.setAttribute("successMessage", "Event created successfully!");
                response.sendRedirect(request.getContextPath() + "/events?eventId=" + newEvent.getId());
            } catch (IllegalArgumentException e) {
                session.setAttribute("errorMessage", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/events?action=create");
            }

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }
}
