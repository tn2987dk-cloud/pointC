package pointC.login;

import java.io.IOException;
import common.JDBConnect; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/login.do")
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 폼에서 입력받은 값 가져오기
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");

        // 2. DAO를 통해 DB에 해당 회원이 있는지 확인
        MemberDAO dao = new MemberDAO();
        MemberDTO memberDto = dao.getMember(userId, password);
        dao.close(); // DB 연결 해제 (필수!)

        // 3. 로그인 성공 여부 판단
        if (memberDto.getId() != null) { 
            // DTO에 ID가 있다는 건 DB에서 찾았다는 뜻! (로그인 성공)

            HttpSession session = req.getSession();
            
            // [중요] 세션에 아이디 저장
            session.setAttribute("userId", memberDto.getId());
            
            // ✅ [수정] 이름 저장 (변수명 memberDto 사용)
            // 아까 dto.getName() 이라고 썼던 줄은 지웠습니다!
            session.setAttribute("userName", memberDto.getName()); 
            
            // 메인 화면으로 이동
            resp.sendRedirect(req.getContextPath() + "/main.do");

        } else {
            // 로그인 실패 (DB에 없거나 비번 틀림)
            req.setAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            req.getRequestDispatcher("/pointC/login/login.jsp").forward(req, resp);
        }
    }
}