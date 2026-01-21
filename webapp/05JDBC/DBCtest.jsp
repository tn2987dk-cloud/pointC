<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="common.JDBConnect"%>
<%@page import="java.sql.PreparedStatement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
JDBConnect jdbc = new JDBConnect();
String ID = request.getParameter("Uid");
String PW = request.getParameter("Upw");
String Name = request.getParameter("Uname");

String sql = "INSERT INTO member VALUES (?, ?, ?, sysdate)";
PreparedStatement psmt = jdbc.con.prepareStatement(sql);
psmt.setString(1, ID);
psmt.setString(2, PW);
psmt.setString(3, Name);
psmt.executeUpdate();   // 실행 추가
psmt.close();

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>결과 보기</title>
</head>
<body>
<h2>회원 목록 조회 테스트</h2>

<%
String sql1 = "SELECT id, pass, name FROM member";
Statement stmt = jdbc.con.createStatement();
ResultSet rs = stmt.executeQuery(sql1);

while (rs.next()) {
    String id = rs.getString("id");
    String pw = rs.getString("pass");
    String name = rs.getString("name");
    out.println(String.format("%s %s %s", id, pw, name) + "<br/>");
}

rs.close();
stmt.close();
jdbc.close();
%>
</body>
</html>