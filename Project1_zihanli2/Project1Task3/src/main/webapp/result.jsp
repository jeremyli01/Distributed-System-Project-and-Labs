<%@ page import="static ds.project1task3.HelloServlet.choiceMap" %><%--
  Created by IntelliJ IDEA.
  User: jeremyli
  Date: 2/7/23
  Time: 2:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Result</title>
</head>
<body>
<h1>Distributed Systems Class Clicker</h1><br>
<% if (choiceMap.get("A") == 0 && choiceMap.get("B") == 0 && choiceMap.get("C") == 0 && choiceMap.get("D") == 0) { %>
<h2>There are currently no results</h2><br>
<% } else {%>
<h2>The results from the survey are as follows:</h2><br>
<h2>A: <%= choiceMap.get("A")%></h2>
<h2>B: <%= choiceMap.get("B")%></h2>
<h2>C: <%= choiceMap.get("C")%></h2>
<h2>D: <%= choiceMap.get("D")%></h2>
<% } %>
</body>
</html>
