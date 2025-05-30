<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - LearnHub</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <%-- Removed inline styles, will rely on style.css and navbar.jspf styles --%>
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="container">
    <%-- The welcome message is now part of navbar.jspf --%>
    <h1>Welcome to Your LearnHub Dashboard!</h1>
    <p>This is your central hub for all activities. Use the navigation bar above to explore different sections.</p>

    <div style="border: 1px solid #eee; margin: 10px; padding: 10px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05);">
        <h3>Today's Coding Challenge</h3>
        <p>A new coding challenge is available each day. Click the link below to view and solve it!</p>
        <div style="background-color: #f9f9f9; padding: 8px; margin-top: 5px; border-radius: 4px; text-align: center;">
            <a href="${pageContext.request.contextPath}/dsa/today" class="button-link-custom">View Today's Challenge</a>
        </div>
    </div>

    <div style="border: 1px solid #eee; margin: 10px; padding: 10px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05);">
        <h3>Latest Educational Posts</h3>
        <p>Explore a variety of articles and video tutorials to enhance your knowledge. Click the link below to see all available posts.</p>
        <div style="background-color: #f9f9f9; padding: 8px; margin-top: 5px; border-radius: 4px; text-align: center;">
            <a href="${pageContext.request.contextPath}/posts/list" class="button-link-custom info">View All Educational Posts</a>
        </div>
    </div>

    <div style="border: 1px solid #eee; margin: 10px; padding: 10px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05);">
        <h3>Community Events & Quizzes</h3>
        <p>Stay updated on upcoming study sessions, tech talks, quizzes, and other community events. You can also create your own events!</p>
         <div style="background-color: #f9f9f9; padding: 8px; margin-top: 5px; border-radius: 4px; text-align: center;">
            <a href="${pageContext.request.contextPath}/events" class="button-link-custom info">View & Create Events</a>
            <!-- <a href="#" class="button-link-custom warning" style="margin-left:10px;">View Quizzes</a> -->
        </div>
    </div>
    
    <div style="border: 1px solid #eee; margin: 10px; padding: 10px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.05);">
        <h3>Community Polls</h3>
        <p>Engage with the community by creating polls or voting on existing ones. Click the link below to view all polls or create your own.</p>
        <div style="background-color: #f9f9f9; padding: 8px; margin-top: 5px; border-radius: 4px; text-align: center;">
             <a href="${pageContext.request.contextPath}/polls" class="button-link-custom success">View & Create Polls</a>
        </div>
    </div>
</div>

</body>
</html>
