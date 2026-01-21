<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="pointC.login.MemberDTO" %>
<%
    MemberDTO member = (MemberDTO) request.getAttribute("member");
    if (member == null) {
        // 데이터 없으면 컨트롤러로 다시 보냄
        response.sendRedirect(request.getContextPath() + "/mypage");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/pointC/css/main.css">
<style>
    /* 마이페이지 전용 스타일 */
    .mypage-container { max-width: 600px; margin: 40px auto; }
    .form-group { margin-bottom: 20px; }
    .form-label { display: block; font-weight: bold; margin-bottom: 8px; color: #555; }
    .form-input { width: 100%; height: 44px; padding: 0 12px; border: 1px solid #d6dae3; border-radius: 10px; font-size: 15px; }
    .form-input[readonly] { background-color: #f1f3f5; color: #888; cursor: not-allowed; }
    .btn-group { display: flex; gap: 10px; margin-top: 30px; }
    .danger-link { color: #fa5252; font-size: 13px; text-decoration: underline; cursor: pointer; border: none; background: none; margin-top: 20px;}
    .muted { font-size: 12px; color: #888; margin-top: 5px; }
</style>
</head>
<body>

<header class="topbar">
  <div class="brand">POINT:C</div>
  <div class="top-actions">
    <button class="ghost-btn" onclick="location.href='<%=request.getContextPath()%>/main.do'">메인</button>
    <button class="ghost-btn" onclick="location.href='<%=request.getContextPath()%>/logout.do'">로그아웃</button>
  </div>
</header>

<main class="page">
    <div class="mypage-container">
        <section class="card">
            <div class="card-head">
                <h2>내 정보 수정</h2>
            </div>
            
            <div class="card-body">
                <form action="<%=request.getContextPath()%>/mypage" method="post">
                    
                    <div class="form-group">
                        <label class="form-label">아이디</label>
                        <input type="text" class="form-input" value="<%= member.getId() %>" readonly>
                        <p class="muted">아이디는 변경할 수 없습니다.</p>
                    </div>

                    <div class="form-group">
                        <label class="form-label">이름 (닉네임)</label>
                        <input type="text" name="name" class="form-input" value="<%= member.getName() %>" required>
                    </div>

                    <hr style="margin: 25px 0; border: 0; border-top: 1px solid #eee;">
                    <p class="muted" style="margin-bottom:15px; font-weight:bold; color:#4a6cf7;">※ 정확한 자세 분석을 위한 신체 정보</p>

                    <div style="display:flex; gap:10px;">
                        <div class="form-group" style="flex:1;">
                            <label class="form-label">키 (cm)</label>
                            <input type="number" step="0.1" name="height" class="form-input" 
                                   value="<%= member.getHeight() %>" placeholder="예: 175.5">
                        </div>
                        <div class="form-group" style="flex:1;">
                            <label class="form-label">몸무게 (kg)</label>
                            <input type="number" step="0.1" name="weight" class="form-input" 
                                   value="<%= member.getWeight() %>" placeholder="예: 65.0">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">성별</label>
                        <select name="gender" class="form-input">
                            <option value="">선택안함</option>
                            <option value="M" <%= "M".equals(member.getGender()) ? "selected" : "" %>>남성</option>
                            <option value="F" <%= "F".equals(member.getGender()) ? "selected" : "" %>>여성</option>
                        </select>
                    </div>

                    <hr style="margin: 25px 0; border: 0; border-top: 1px solid #eee;">

                    <div class="form-group">
                        <label class="form-label">비밀번호 변경</label>
                        <input type="text" name="pass" class="form-input" value="<%= member.getPass() %>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">가입일</label>
                        <input type="text" class="form-input" value="<%= member.getRegidate() %>" readonly>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="primary-btn">정보 수정 저장</button>
                    </div>
                </form>

                <div style="text-align: right;">
                    <form action="<%=request.getContextPath()%>/withdraw.do" method="post" style="display:inline;">
                        <button type="submit" class="danger-link" onclick="return confirm('정말 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.');">
                            회원 탈퇴하기
                        </button>
                    </form>
                </div>
            </div>
        </section>
    </div>
</main>

</body>
</html>