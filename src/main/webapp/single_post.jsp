<%@ page import="com.example.model.EducationalPost" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Educational Post - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navbar.jspf" %>
    <div class="container">
        <c:set var="post" value="${educationalPost}" />
        <%-- Removed old navbar and back link container --%>

        <c:choose>
            <c:when test="${not empty post}">
                <h1>${post.title}</h1>
                <div class="post-meta">
                    <span>Author: ${post.author}</span>
                    <span>Posted: <fmt:formatDate value="${post.datePosted}" pattern="MMMM dd, yyyy 'at' hh:mm a" /></span>
                    <span>Type: ${post.type}</span>
                </div>

                <div class="post-content">
                    <c:choose>
                        <c:when test="${post.type eq 'video_link'}">
                            <p>${post.content}</p>
                            <c:if test="${not empty post.videoUrl}">
                                <c:choose>
                                    <c:when test="${post.videoUrl.contains('youtube.com/embed')}">
                                        <div class="video-container">
                                            <iframe src="${post.videoUrl}" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p>Watch the video: <a href="${post.videoUrl}" target="_blank" class="button-link-custom success">Open Video</a></p>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:when>
                        <c:otherwise> 
                            <p>${post.content}</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <p class="message error">The requested educational post could not be found.</p>
            </c:otherwise>
        </c:choose>

        <div class="back-link-container" style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/posts/list" class="button-link-custom">&laquo; Back to All Posts</a>
        </div>
    </div>
</body>
</html>
