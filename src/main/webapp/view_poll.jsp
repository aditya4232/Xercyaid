<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Poll - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style> /* Specific styles for progress bar, can be moved to style.css later */
        .progress-bar-container { background-color: #e9ecef; border-radius: .25rem; height: 20px; margin-top: 5px; overflow: hidden;}
        .progress-bar { background-color: #007bff; height: 100%; line-height: 20px; color: white; text-align: center; border-radius: .25rem; white-space: nowrap; font-size: 0.9em; transition: width 0.6s ease; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <c:set var="currentPoll" value="${poll}" />
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
            <c:when test="${not empty currentPoll}">
                <h1>${currentPoll.question}</h1>
                <div class="poll-meta">
                    <span>Created by: ${currentPoll.createdBy}</span>
                    <span>Created on: <fmt:formatDate value="${currentPoll.dateCreated}" pattern="MMMM dd, yyyy 'at' hh:mm a" /></span>
                </div>

                <form action="${pageContext.request.contextPath}/polls" method="post" class="vote-form form-group">
                    <input type="hidden" name="action" value="vote">
                    <input type="hidden" name="pollId" value="${currentPoll.id}">
                    <label>Cast your vote:</label>
                    <c:forEach var="option" items="${currentPoll.options}">
                        <div class="form-group">
                            <input type="radio" name="optionId" value="${option.id}" id="opt_${option.id}" required>
                            <label for="opt_${option.id}">${option.text}</label>
                        </div>
                    </c:forEach>
                    <input type="submit" value="Submit Vote" class="button-link-custom">
                </form>

                <div class="results-section">
                    <h2>Current Results</h2>
                    <c:set var="totalVotes" value="${currentPoll.totalVotes}" />
                    <c:choose>
                        <c:when test="${totalVotes > 0}">
                             <p>Total votes: ${totalVotes}</p>
                            <c:forEach var="option" items="${currentPoll.options}">
                                <div class="option-result">
                                    <span class="option-text">${option.text}</span> - ${option.votes} vote(s)
                                    <c:set var="percentage" value="${option.votes * 100.0 / totalVotes}" />
                                    <div class="progress-bar-container">
                                        <div class="progress-bar" style="width: <fmt:formatNumber value="${percentage}" maxFractionDigits="0"/>%;" role="progressbar" aria-valuenow="<fmt:formatNumber value="${percentage}" maxFractionDigits="0"/>" aria-valuemin="0" aria-valuemax="100">
                                            <fmt:formatNumber value="${percentage}" maxFractionDigits="0"/>%
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="no-votes message">No votes have been cast yet for this poll.</p>
                        </c:otherwise>
                    </c:choose>
                </div>

            </c:when>
            <c:otherwise>
                <p class="message error">The requested poll could not be found or is no longer available.</p>
            </c:otherwise>
        </c:choose>

        <div class="back-link-container" style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/polls" class="button-link-custom">&laquo; Back to All Polls</a>
        </div>
    </div>
</body>
</html>
