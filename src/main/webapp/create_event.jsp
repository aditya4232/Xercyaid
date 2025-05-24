<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create New Event - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <h1>Create a New Event</h1>
        <%-- Removed old navbar and back link container --%>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <form action="${pageContext.request.contextPath}/events" method="post" class="form-group">
            <input type="hidden" name="action" value="create">

            <div class="form-group">
                <label for="title">Event Title:</label>
                <input type="text" id="title" name="title" required>
            </div>

            <div class="form-group">
                <label for="description">Event Description:</label>
                <textarea id="description" name="description" required></textarea>
            </div>

            <div class="form-group">
                <label for="eventDate">Event Date and Time:</label>
                <input type="datetime-local" id="eventDate" name="eventDate" required>
            </div>

            <div class="form-group">
                <label for="location">Location (optional):</label>
                <input type="text" id="location" name="location" placeholder="e.g., Online, Conference Room B">
            </div>

            <div class="form-group" style="text-align: center;">
                <input type="submit" value="Create Event" class="button-link-custom info">
            </div>
        </form>

        <div class="back-link-container" style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/events" class="button-link-custom">&laquo; Back to All Events</a>
        </div>
    </div>
</body>
</html>
