<%--
  Created by IntelliJ IDEA.
  User: jeremyli
  Date: 2/6/23
  Time: 9:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search Result</title>
</head>
<body>
<form method="GET" action="${pageContext.request.contextPath}/">
    <h1>Country: <%= request.getAttribute("country")%></h1><br>
    <h2>Nickname: <%= request.getAttribute("nickname")%></h2>
    <b>www.topendsports.com/sport/soccer/team-nicknames-women.htm</b><br>
    <h2>Capital City: <%= request.getAttribute("capital")%></h2>
    <b>www.restcountries.com</b><br>
    <h2>Top Scorers in 2019: <%= request.getAttribute("topscorer")%>, <%=request.getAttribute("topscore")%> goals</h2>
    <b>www.espn.com/soccer/stats/_/league/FIFA.WWC/season/2019/view/scoring</b><br>
    <h2>Flag: </h2> <img style="height: 100px; width: auto" src="<%= request.getAttribute("flag")%>"><br>
    <b>www.cia.gov/the-world-factbook/countries</b><br>
    <h2>Flag Emoji: </h2> <div style="font-size: 150px"><%= request.getAttribute("emoji")%></div><br>
    <b>www.cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/your-country-here.svg</b><br>

    <input type="submit" value="Continue">
</form>
</body>
</html>
