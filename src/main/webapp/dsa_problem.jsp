<%@ page import="com.example.model.DsaProblem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>DSA Coding Challenge - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <%
            DsaProblem problem = (DsaProblem) request.getAttribute("dsaProblem");
            String message = (String) session.getAttribute("dsaMessage"); // Retrieve message from session
            if (message != null) {
                session.removeAttribute("dsaMessage"); // Clear message after displaying
            }
        %>

        <h1>Today's Coding Challenge</h1>

        <c:if test="${not empty message}">
             <p class="message success"><c:out value="${message}"/></p>
        </c:if>


        <c:choose>
            <c:when test="${not empty dsaProblem}">
                <h2><c:out value="${dsaProblem.title}"/></h2>
                <p><c:out value="${dsaProblem.description}"/></p>

                <h3>Example Input:</h3>
                <pre><c:out value="${dsaProblem.exampleInput}"/></pre>

                <h3>Example Output:</h3>
                <pre><c:out value="${dsaProblem.exampleOutput}"/></pre>

                <form action="${pageContext.request.contextPath}/dsa/submitSolution" method="post" class="form-group">
                    <input type="hidden" name="problemId" value="<c:out value="${dsaProblem.id}"/>">
                    <div class="form-group">
                        <label for="solution"><h3>Your Solution:</h3></label>
                        <textarea id="solution" name="solution" required placeholder="Enter your code here..."></textarea>
                    </div>
                    <div>
                        <input type="submit" value="Submit Solution" class="button-link-custom">
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                 <p class="message error">Could not load the DSA problem. Please try again later.</p>
            </c:otherwise>
        </c:choose>
        <%-- "Back to Dashboard" link is now in navbar.jspf --%>
    </div>
</body>
</html>
