<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/pointC/css/main.css">
</head>
<body>

<div class="login-wrapper">
  <div class="login-box">
    <h2>회원가입</h2>

    <form action="<%=request.getContextPath()%>/signup.do" method="post">

      <input type="text" name="userId" placeholder="아이디" required>

      <input type="password" name="password" placeholder="비밀번호" required>

      <input type="password" name="passwordConfirm" placeholder="비밀번호 확인" required>

      <input type="text" name="name" placeholder="이름" required>

      <button type="submit" class="login-btn">회원가입</button>
    </form>

    <button class="signup-btn"
      onclick="location.href='<%=request.getContextPath()%>/pointC/login/login.jsp'">
      로그인으로
    </button>
  </div>
</div>

</body>
</html>
