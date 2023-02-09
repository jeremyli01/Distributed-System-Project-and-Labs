        <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.io.File"%>
<%@page import="java.util.Scanner"%>
<%@page import="java.io.FileNotFoundException"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>Welcome Page</title>
</head>
<body>
<h1>Women's World Cup 2023</h1>
<b>Created by Zihan Li</b><br/>
<h2>Participating Countries</h2>
<form method="get" action="${pageContext.request.contextPath}/hello-servlet">
    <label>Choose a country:</label>
    <select id="countries" name="countries">
        <%List<String> countryList = new ArrayList<>();%>
        <%   try { %>
        <%File file = new File("/Users/jeremyli/Documents/95-702 Distributed System/Project1_zihanli2/Project1Task2/src/main/webapp/countries.txt");%>
        <%Scanner scanner = new Scanner(file);%>
        <%while (scanner.hasNextLine()) {%>
        <%    countryList.add(scanner.nextLine());%>
        <%}%>
        <%scanner.close();%>
        <%    } catch (FileNotFoundException e) {%>
        <%e.printStackTrace();%>
        <%}%>
        <% for (String country : countryList) {%>
        <option value="<%=country%>"><%=country%></option>
        <%}%>
    </select>
    <input type="submit" value="Submit">
</form>
</body>
</html>