package pointC.log;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/saveLog.do")
public class LogSaveController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. JSP에서 보낸 데이터 받기
        req.setCharacterEncoding("UTF-8");
        String userId = req.getParameter("userId");
        String message = req.getParameter("message"); // "5분 이상..." (글자)
        String type = req.getParameter("type");       // "WARN"
        
        // 2. DB에 저장
        LogDAO dao = new LogDAO();
        LogDTO dto = new LogDTO();
        
        dto.setUserId(userId);
        dto.setMessage(message);
        dto.setType(type);
        
        int result = dao.insertLog(dto); // 알림 저장 메서드 호출
        dao.close();
        
        // 3. 응답 (성공 여부만 전송)
        resp.getWriter().write(result > 0 ? "SUCCESS" : "FAIL");
    }
}