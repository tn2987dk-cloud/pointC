<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/pointC/css/main.css">
</head>
<body>
<%
  String msg = request.getParameter("msg");
  if ("withdrawn".equals(msg)) {
%>
  <div style="max-width:420px;margin:0 auto 12px;color:#0b6; font-weight:700;">
    회원 탈퇴가 완료되었습니다. (DB 연결 전: 세션 기반 처리)
  </div>
<% } %>

<div class="login-wrapper">
  <div class="login-box">
    <h2>로그인</h2>
    <form action="<%=request.getContextPath()%>/login.do" method="post">
      <input type="text" name="userId" placeholder="아이디" required>
      <input type="password" name="password" placeholder="비밀번호" required>

      <button type="submit" class="login-btn">로그인</button>
    </form>

    <button class="signup-btn"
      onclick="location.href='<%=request.getContextPath()%>/pointC/login/signup.jsp'">
      회원가입
    </button>
  </div>
</div>

</body>
</html>
