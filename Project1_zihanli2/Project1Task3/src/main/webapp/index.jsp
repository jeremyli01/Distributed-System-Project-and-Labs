<%@ page import="static ds.project1task3.HelloServlet.lastChoice" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to Class Clicker</title>
</head>
<body>
<h1>Distributed Systems Class Clicker</h1><br>
<% if (!lastChoice.equals("")) { %>
<h2>Your "<%=lastChoice%>" has been registered</h2><br>
<% }%>
<h2>Submit your answer to the current question:</h2><br>
<form method="get" action="${pageContext.request.contextPath}/submit">
    <input type = "radio" name = "choice" value = "A"/> A<br>
    <input type = "radio" name = "choice" value = "B"/> B<br>
    <input type = "radio" name = "choice" value = "C"/> C <br>
    <input type = "radio" name = "choice" value = "D"/> D <br>
    <input type="submit" name="">
</form>

</body>
</html>