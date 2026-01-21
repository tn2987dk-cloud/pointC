package pointC;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();

        // ✅ 공개 경로 설정 (잘 하셨어요!)
        boolean isPublic = 
                uri.startsWith(ctx + "/pointC/login/") || 
                uri.startsWith(ctx + "/pointC/css/") ||    
                uri.startsWith(ctx + "/resources/") ||
                uri.equals(ctx + "/") ||
                uri.equals(ctx + "/login.do") ||
                uri.equals(ctx + "/signup.do") ||
                uri.equals(ctx + "/logout.do");

        // ✅ [수정 1] 세션 이름 소문자 "userId"로 변경
        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("userId") != null);

        // ✅ [수정 2] 로그인 상태에서 로그인 페이지 접근 시, 실제 메인 페이지로 이동
        if (loggedIn && uri.equals(ctx + "/pointC/login/login.jsp")) {
            resp.sendRedirect(ctx + "/pointC/main/mainpage.jsp");
            return;
        }

        // 공개 페이지는 통과
        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // 비공개 페이지는 로그인 필요
        if (loggedIn && uri.equals(ctx + "/pointC/login/login.jsp")) {
            // ✅ MainController를 거쳐야 DB에서 투두 리스트를 챙겨서 화면에 뿌려줍니다.
            resp.sendRedirect(ctx + "/main.do"); 
            return;
        }

        chain.doFilter(request, response);
    }
}