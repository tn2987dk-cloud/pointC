package pointC.todo;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/todo.do")
public class TodoController extends HttpServlet {

    // [수정] GET으로 들어와도 action이 있으면 doPost로 토스! (이게 핵심)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // 동작(삭제,토글 등)이 있는 경우 -> doPost로 넘겨서 처리
        if (action != null && !action.isEmpty()) {
            doPost(req, resp);
        } else {
            // 동작 없이 그냥 들어온 경우 -> 메인으로 쫓아냄
            resp.sendRedirect(req.getContextPath() + "/main.do");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");

        // 로그인 안 했으면 로그인 페이지로
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        String dateStr = req.getParameter("date");
        
        // [디버깅] 콘솔 확인용 (이클립스 Console창을 보세요!)
        System.out.println("TodoController 호출됨 - action: " + action + ", date: " + dateStr);

        TodoDAO dao = new TodoDAO();

        if ("add".equals(action)) {
            String content = req.getParameter("content");
            if (content != null && !content.trim().isEmpty()) {
                TodoDTO dto = new TodoDTO();
                dto.setUserId(userId);
                dto.setContent(content);
                // 날짜가 있으면 그 날짜로, 없으면 오늘
                if (dateStr != null && !dateStr.isEmpty()) {
                    dto.setPostdate(java.sql.Date.valueOf(dateStr));
                } else {
                    dto.setPostdate(new java.sql.Date(System.currentTimeMillis()));
                }
                dao.insertTodo(dto);
            }
        } 
        else if ("toggle".equals(action)) {
            String idx = req.getParameter("idx");
            System.out.println("토글 시도 - idx: " + idx); // 로그 확인
            dao.toggleTodo(idx);
        }
        else if ("delete".equals(action)) {
            String idx = req.getParameter("idx");
            System.out.println("삭제 시도 - idx: " + idx); // 로그 확인
            dao.deleteTodo(idx);
        }

        dao.close();

        // 작업 끝난 후 날짜 유지해서 돌아가기
        if (dateStr != null && !dateStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/main.do?date=" + dateStr);
        } else {
            resp.sendRedirect(req.getContextPath() + "/main.do");
        }
    }
}