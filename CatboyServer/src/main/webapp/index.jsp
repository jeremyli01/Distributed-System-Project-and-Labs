<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "This is the root address of the server" %>
</h1>
<br/>
<h2><%= "To get a new picture: /getCatboy?type=catboy" %></h2>
<h2><%= "To the dashboard: /getCatboy?type=dashboard" %></h2>

<a href="getCatboy?type=dashboard">Dashboard</a>
<a href="getCatboy?type=catboy">Get Catboy</a>
</body>
</html>