package pointC.login;

import java.io.IOException;
import common.JDBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/withdraw.do")
public class DeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. 현재 로그인된 사용자 확인 (세션에서 ID 꺼내기)
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId"); // 로그인할 때 저장한 이름("userId" 또는 "loginUser")으로 꺼내야 함!
        
        // [주의] 세션에 저장된 이름이 헷갈린다면? 
        // LoginController에서 session.setAttribute("userId", ...); 라고 했는지 꼭 확인하세요.
        // 만약 DTO 자체를 저장했다면: MemberDTO dto = (MemberDTO) session.getAttribute("loginUser"); userId = dto.getId();

        int result = 0;
        
        if (userId != null) {
            // 2. DB 연결 및 삭제 시도
            MemberDAO dao = new MemberDAO();
            result = dao.deleteMember(userId);
            dao.close(); // 자원 해제
        }

        // 3. 결과 처리
        if (result == 1) { // 삭제 성공
            session.invalidate(); // 세션 삭제 (로그아웃 처리)
            // 탈퇴 완료 메시지를 띄우기 위해 msg 파라미터 전달
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp?msg=withdrawn");
        } 
        else { // 삭제 실패 (로그인이 안 되어 있거나 DB 오류)
            // 실패 알림 후 뒤로 가기 (JS 사용)
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("<script>alert('탈퇴에 실패했습니다.'); history.back();</script>");
        }
    }
}