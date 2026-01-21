<%@page import="java.sql.PreparedStatement"%>
<%@page import="common.JDBConnect"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JDBC</title>
</head>
<body>
	
	<h2>회원 추가 테스트 2</h2>
	<form action="DBCtest.jsp" method="post">
		아이디 : <input type="text" name="Uid" /> <br/>
		패스워드 : <input type="password" name="Upw" /> <br/>
		이름 : <input type="text" name ="Uname" /> <br/>
	<input type="submit" value="전송하기">
	</form>
	
	<h2>쿠키냠냠</h2>
	<form action = "Cookie.jsp">
	이름 : <input type="text" name="cookie1" /><br/>
	<input type="submit" value="전송하기" name="cookievalue"/>
	</form>
</body>
</html>