<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Available Polls - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <h1>Polls</h1>
        <%-- Removed old navbar and dashboard link container --%>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <div class="create-poll-link-container" style="text-align: center; margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/polls?action=create" class="button-link-custom success">Create New Poll</a>
        </div>

        <c:choose>
            <c:when test="${not empty pollList}">
                <c:forEach var="poll" items="${pollList}">
                    <div class="poll-item dashboard-section"> <%-- Added dashboard-section for styling --%>
                        <h2><a href="${pageContext.request.contextPath}/polls?pollId=${poll.id}">${poll.question}</a></h2>
                        <div class="poll-meta">
                            Created by: ${poll.createdBy} on <fmt:formatDate value="${poll.dateCreated}" pattern="MMMM dd, yyyy" />
                        </div>
                        <div class="poll-meta">
                            <c:out value="${poll.options.size()}" /> options, <c:out value="${poll.totalVotes}" /> total votes.
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p class="no-polls message">No polls available at the moment. Why not create one?</p>
            </c:otherwise>
        </c:choose>
        <%-- "Back to Dashboard" link is now in navbar.jspf --%>
    </div>
</body>
</html>
