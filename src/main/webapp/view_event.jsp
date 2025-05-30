<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Event Details - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <c:set var="currentEvent" value="${event}" />
        <%-- Removed old navbar and back link container --%>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <c:choose>
            <c:when test="${not empty currentEvent}">
                <h1>${currentEvent.title}</h1>
                <div class="event-detail">
                    <strong>Date & Time:</strong>
                    <fmt:formatDate value="${currentEvent.eventDate}" pattern="EEEE, MMMM dd, yyyy 'at' hh:mm a z" />
                </div>
                <div class="event-detail">
                    <strong>Location:</strong>
                    ${not empty currentEvent.location ? currentEvent.location : 'N/A'}
                </div>
                <div class="event-detail">
                    <strong>Created by:</strong>
                    ${currentEvent.createdBy}
                </div>
                <div class="event-detail">
                    <strong>Posted on:</strong>
                    <fmt:formatDate value="${currentEvent.dateCreated}" pattern="MMMM dd, yyyy" />
                </div>
                <div class="event-detail">
                    <strong>Description:</strong>
                </div>
                <div class="event-description">
                    ${currentEvent.description}
                </div>
                
                <div class="rsvp-placeholder dashboard-section" style="text-align:center;"> <%-- Added dashboard-section for styling --%>
                    <p><em>(RSVP functionality will be available here in the future.)</em></p>
                </div>

            </c:when>
            <c:otherwise>
                <p class="message error">The requested event could not be found.</p>
            </c:otherwise>
        </c:choose>

        <div class="back-link-container" style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/events" class="button-link-custom">&laquo; Back to All Events</a>
        </div>
    </div>
</body>
</html>
