<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create New Poll - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <h1>Create a New Poll</h1>
        <%-- Removed old navbar and back link container --%>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <form action="${pageContext.request.contextPath}/polls" method="post" class="form-group">
            <input type="hidden" name="action" value="create">
            <div class="form-group">
                <label for="question">Poll Question:</label>
                <textarea id="question" name="question" required></textarea>
            </div>

            <div class="form-group options-group">
                <label>Poll Options (provide at least 2):</label>
                <input type="text" name="option1" placeholder="Option 1 (required)" class="form-group">
                <input type="text" name="option2" placeholder="Option 2 (required)" class="form-group">
                <input type="text" name="option3" placeholder="Option 3 (optional)" class="form-group">
                <input type="text" name="option4" placeholder="Option 4 (optional)" class="form-group">
            </div>

            <div class="form-group" style="text-align: center;">
                <input type="submit" value="Create Poll" class="button-link-custom success">
            </div>
        </form>

        <div class="back-link-container" style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/polls" class="button-link-custom">&laquo; Back to All Polls</a>
        </div>
    </div>
</body>
</html>
