<%@ page import="com.example.model.EducationalPost" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Educational Posts - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <h1>All Educational Posts</h1>
        <%-- Removed old navbar and dashboard link container --%>

        <c:choose>
            <c:when test="${not empty postList}">
                <c:forEach var="post" items="${postList}">
                    <div class="post-entry dashboard-section"> <%-- Added dashboard-section for styling --%>
                        <h2><a href="${pageContext.request.contextPath}/posts?id=${post.id}">${post.title}</a></h2>
                        <div class="post-meta">
                            <span>Author: ${post.author}</span>
                            <span>Posted: <fmt:formatDate value="${post.datePosted}" pattern="MMMM dd, yyyy 'at' hh:mm a" /></span>
                            <span>Type: ${post.type}</span>
                        </div>
                        <div class="post-content-snippet">
                            <c:choose>
                                <c:when test="${post.type eq 'video_link'}">
                                    <p>This is a video post. Click below to view more details or watch the video.</p>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="content" value="${post.content}" />
                                    <c:choose>
                                        <c:when test="${content.length() > 200}">
                                            <p>${content.substring(0, 200)}...</p>
                                        </c:when>
                                        <c:otherwise>
                                            <p>${content}</p>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <a href="${pageContext.request.contextPath}/posts?id=${post.id}" class="button-link-custom">
                            <c:choose>
                                <c:when test="${post.type eq 'video_link'}">View Details</c:when>
                                <c:otherwise>Read More</c:otherwise>
                            </c:choose>
                        </a>
                        <c:if test="${post.type eq 'video_link' and not empty post.videoUrl}">
                            <a href="${post.videoUrl}" target="_blank" class="button-link-custom success" style="margin-left: 10px;">Watch Video Directly</a>
                        </c:if>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p class="no-posts message">No educational posts found at the moment. Please check back later!</p>
            </c:otherwise>
        </c:choose>
        <%-- "Back to Dashboard" link is now in navbar.jspf --%>
    </div>
</body>
</html>
