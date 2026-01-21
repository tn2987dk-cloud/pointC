package pointC.login;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/signup.do")
public class SignupController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 입력값 받기
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");
        String name = req.getParameter("name");

        // 2. 비밀번호 확인 (DB 가기 전에 1차 검증)
        if (!password.equals(passwordConfirm)) {
            req.setAttribute("error", "비밀번호가 일치하지 않습니다.");
            req.getRequestDispatcher("/pointC/login/signup.jsp").forward(req, resp);
            return;
        }

        // 3. DB에 회원 저장 시도
        MemberDTO dto = new MemberDTO();
        dto.setId(userId);
        dto.setPass(password);
        dto.setName(name);

        MemberDAO dao = new MemberDAO();
        int result = dao.insertMember(dto); // 성공하면 1, 실패하면 0
        dao.close();

        // 4. 결과 처리
        if (result == 1) {
            // 회원가입 성공 -> 로그인 페이지로 이동
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
        } else {
            // 회원가입 실패 (예: 아이디 중복 등)
            req.setAttribute("error", "회원가입에 실패했습니다. (이미 존재하는 아이디일 수 있습니다)");
            req.getRequestDispatcher("/pointC/login/signup.jsp").forward(req, resp);
        }
    }
}