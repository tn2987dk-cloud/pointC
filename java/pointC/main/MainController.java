package pointC.main;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections; // [추가] 리스트 뒤집기용
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pointC.log.LogDAO;  // [추가]
import pointC.log.LogDTO;  // [추가]
import pointC.todo.TodoDAO;
import pointC.todo.TodoDTO;

@WebServlet("/main.do")
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        // 1. 날짜 처리
        String selectedDate = req.getParameter("date");
        LocalDate today = LocalDate.now();
        if (selectedDate == null) selectedDate = today.toString();
        
        LocalDate dateObj = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE);
        String currentMonthStr = dateObj.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 2. 투두 & 캘린더 통계
        TodoDAO todoDao = new TodoDAO();
        List<TodoDTO> myTodoList = todoDao.selectTodoList(userId, selectedDate);
        Map<String, Integer> calendarStats = todoDao.getMonthStats(userId, currentMonthStr);
        todoDao.close();

        /// =========================================================
        // [3] DB 데이터 가져오기 (그래프용 vs 알림목록용 분리!)
        // =========================================================
        LogDAO logDao = new LogDAO();
        
        // 1. 그래프 그릴 데이터 (sensor_data 테이블 - 아까 더미데이터 넣은 곳!)
        List<LogDTO> graphSourceList = logDao.selectGraphData(userId); 
        
        // 2. 우측 하단 알림창에 띄울 데이터 (posture_log 테이블)
        // (만약 selectRecentLogs 메서드가 없으면 이 줄은 지우거나 주석 처리하세요)
        List<LogDTO> logList = logDao.selectRecentLogs(userId); 
        
        logDao.close();

        // =========================================================
        // [1] 그래프 데이터 준비 (graphSourceList 사용)
        // =========================================================
        
        // 리스트 뒤집기 (과거 -> 현재)
        List<LogDTO> graphList = new ArrayList<>(graphSourceList);
        Collections.reverse(graphList); 

        StringBuilder labels = new StringBuilder();
        StringBuilder data = new StringBuilder();

        // 날짜 포맷 (일별 데이터를 넣었으니 '월-일'만 보여주는 게 깔끔!)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");

        for (int i = 0; i < graphList.size(); i++) {
            LogDTO dto = graphList.get(i);
            
            // 날짜: '01-15' 형태
            labels.append("'").append(sdf.format(dto.getRegdate())).append("'");
            // 점수: 80
            data.append(dto.getScore());

            if (i < graphList.size() - 1) {
                labels.append(",");
                data.append(",");
            }
        }
        
        // 데이터 유무 처리
        if (graphList.isEmpty()) {
            req.setAttribute("graphLabels", ""); // 빈 값
            req.setAttribute("graphData", "");   // 빈 값
            req.setAttribute("todayScore", 0); 
            req.setAttribute("hasData", false);
        } else {
            req.setAttribute("graphLabels", labels.toString());
            req.setAttribute("graphData", data.toString());
            // [수정] 가장 최근 데이터(마지막꺼)를 오늘 점수로
            req.setAttribute("todayScore", graphList.get(graphList.size()-1).getScore());
            req.setAttribute("hasData", true);
        }

        // =========================================================
        
        // JSP로 전송
        req.setAttribute("todoList", myTodoList);
        req.setAttribute("selectedDate", selectedDate);
        req.setAttribute("currentDateObj", dateObj);
        req.setAttribute("calendarStats", calendarStats);
        
        req.setAttribute("logList", logList);     // 이건 알림 목록용으로 그대로 유지
        req.setAttribute("badPoint", "목/어깨");

        req.getRequestDispatcher("/pointC/main/mainpage.jsp").forward(req, resp);
    }
}