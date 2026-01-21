package pointC.analysis;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/analysisPage.do") // 이 주소로 접속해야 함!
public class AnalysispageController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        
        // 로그인 안 했으면 튕기기
        if(userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        // DB에서 기록 가져오기
        AnalysisDAO dao = new AnalysisDAO();
        List<AnalysisDTO> historyList = dao.selectMyHistory(userId);
        dao.close();

        // JSP로 짐 보내기
        req.setAttribute("historyList", historyList);
        
        // 화면 열기 (경로 주의!)
        req.getRequestDispatcher("/pointC/analysis/analysis.jsp").forward(req, resp);
    }
}