<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>${not empty eventListView ? eventListView : 'All'} Events - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <h1><c:out value="${not empty eventListView ? eventListView : 'All'}"/> Events</h1>
        <%-- Removed old navbar and dashboard link container --%>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <div class="action-bar" style="text-align: center; margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/events?action=create" class="button-link-custom success">Create New Event</a>
            <a href="${pageContext.request.contextPath}/events?view=upcoming" class="button-link-custom">View Upcoming</a>
            <a href="${pageContext.request.contextPath}/events?view=past" class="button-link-custom">View Past</a>
            <a href="${pageContext.request.contextPath}/events?view=all" class="button-link-custom">View All</a>
        </div>

        <c:choose>
            <c:when test="${not empty eventList}">
                <c:forEach var="event" items="${eventList}">
                    <div class="event-item dashboard-section"> <%-- Added dashboard-section for styling --%>
                        <h2><a href="${pageContext.request.contextPath}/events?eventId=${event.id}">${event.title}</a></h2>
                        <div class="event-meta">
                            <strong>Date:</strong> <fmt:formatDate value="${event.eventDate}" pattern="MMMM dd, yyyy 'at' hh:mm a z" />
                        </div>
                        <div class="event-meta">
                            <strong>Location:</strong> ${not empty event.location ? event.location : 'N/A'}
                        </div>
                        <div class="event-meta">
                            <strong>Created by:</strong> ${event.createdBy} on <fmt:formatDate value="${event.dateCreated}" pattern="MMMM dd, yyyy" />
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p class="no-events message">No events found for this view. Why not create one?</p>
            </c:otherwise>
        </c:choose>
        <%-- "Back to Dashboard" link is now in navbar.jspf --%>
    </div>
</body>
</html>
