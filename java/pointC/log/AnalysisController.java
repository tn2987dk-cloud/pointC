package pointC.log;

import java.io.IOException;
import java.util.Collections; // 리스트 뒤집기용
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/analysis.do")
public class AnalysisController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. 로그인 체크 및 데이터 가져오기
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        if(userId == null) { resp.sendRedirect("login.jsp"); return; }

        LogDAO dao = new LogDAO();
        List<LogDTO> list = dao.selectGraphData(userId); // DB에서 가져옴
        dao.close();

        // ★ [여기가 핵심!] 리스트를 자바스크립트용 문자열로 변환하기
        StringBuilder labels = new StringBuilder(); // 날짜 담을 곳
        StringBuilder data = new StringBuilder();   // 점수 담을 곳

        // 데이터를 과거 -> 현재 순으로 뒤집기 (그래프는 왼쪽이 과거여야 하니까)
        Collections.reverse(list); 

        for (int i = 0; i < list.size(); i++) {
            LogDTO dto = list.get(i);
            
            // 날짜: 따옴표를 붙여야 함 -> '2025-01-15'
            labels.append("'").append(dto.getRegdate()).append("'");
            
            // 점수: 그냥 숫자만 -> 80
            data.append(dto.getScore());

            // 마지막 데이터가 아니면 콤마(,) 붙이기
            if (i < list.size() - 1) {
                labels.append(",");
                data.append(",");
            }
        }

        // 2. 변환된 문자열을 JSP로 보내기
        // (주의: JSP에서 쓰는 이름과 똑같아야 함!)
        req.setAttribute("graphLabels", labels.toString()); // 예: '1일','2일'
        req.setAttribute("graphData", data.toString());     // 예: 50,60
        
        // 데이터 유무 체크 (JSP 화면 처리용)
        req.setAttribute("hasData", !list.isEmpty());
        if(!list.isEmpty()) {
            req.setAttribute("todayScore", list.get(list.size()-1).getScore()); // 맨 마지막이 오늘 점수
        } else {
            req.setAttribute("todayScore", 0);
        }

        // 3. 이동
        RequestDispatcher rd = req.getRequestDispatcher("mainpage.jsp"); // 파일명 확인하세요! (analysis.jsp 인지 main.jsp 인지)
        rd.forward(req, resp);
    }
}