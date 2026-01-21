package pointC.mypage;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pointC.login.MemberDAO;
import pointC.login.MemberDTO;

@WebServlet("/mypage")
public class MypageController extends HttpServlet {

    // [페이지 보여주기]
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        
        // 로그인 안했으면 쫓아냄
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        // DB에서 최신 정보 가져오기
        MemberDAO dao = new MemberDAO();
        MemberDTO dto = dao.getMemberInfo(userId);
        dao.close();

        // 정보 담아서 JSP로 이동
        req.setAttribute("member", dto);
        req.getRequestDispatcher("/pointC/mypage/mypage.jsp").forward(req, resp);
    }

    // [정보 수정 처리] - 여기가 수정된 핵심입니다!
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        // 1. 화면에서 보낸 모든 정보 받기
        String newPass = req.getParameter("pass");
        String newName = req.getParameter("name");
        String gender = req.getParameter("gender");
        
        // 키, 몸무게는 숫자라서 변환 필요 (빈칸이면 0.0으로 처리)
        double height = 0.0;
        double weight = 0.0;
        try {
            String hStr = req.getParameter("height");
            String wStr = req.getParameter("weight");
            if(hStr != null && !hStr.isEmpty()) height = Double.parseDouble(hStr);
            if(wStr != null && !wStr.isEmpty()) weight = Double.parseDouble(wStr);
        } catch (Exception e) {
            System.out.println("숫자 변환 오류 (무시 가능)");
        }

        // 2. DTO에 한 번에 담기
        MemberDTO dto = new MemberDTO();
        dto.setId(userId);
        dto.setPass(newPass);
        dto.setName(newName);
        dto.setHeight(height); // 추가됨
        dto.setWeight(weight); // 추가됨
        dto.setGender(gender); // 추가됨
        
        // 3. DB 업데이트 실행
        MemberDAO dao = new MemberDAO();
        int result = dao.updateMember(dto);
        dao.close();
        
        // 4. 결과 처리
        if (result == 1) {
            // 수정 성공 시 세션 이름 갱신
            session.setAttribute("userName", newName);
            
            // 알림 띄우고 다시 마이페이지로
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("<script>alert('정보가 수정되었습니다.'); location.href='"+req.getContextPath()+"/mypage';</script>");
        } else {
            // 실패 시 그냥 새로고침
            resp.sendRedirect(req.getContextPath() + "/mypage");
        }
    }
}