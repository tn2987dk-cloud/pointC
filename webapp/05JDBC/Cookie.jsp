<%@page import="common.CookieManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h2>쿠키값 입력</h2>
<form action="setCookie.jsp" method="get">
이름 : <input type="text" name="val1name" />
<input type="submit" value = "전송하기"> <br />
<input type="submit" value="쿠키 삭제하기">
</form>
</body>
</html>