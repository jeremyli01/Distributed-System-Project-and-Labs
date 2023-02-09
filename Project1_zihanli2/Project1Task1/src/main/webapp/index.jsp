<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<form method="get" action="${pageContext.request.contextPath}/computeHashes">
    <input type="text" name="input"><br>
    <input type="radio" name="choice" value="MD5" checked="checked">
    <input type="radio" name="choice" value="SHA-256">
    <input type="submit" value="submit">
</form>
</body>
</html>