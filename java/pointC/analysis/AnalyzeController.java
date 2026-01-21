package pointC.analysis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

// ★ 파일 업로드 처리를 위해 필수! (용량 제한 설정)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 1024 * 1024 * 10,     // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
@WebServlet("/analyze.do")
public class AnalyzeController extends HttpServlet {

    // 파이썬 서버 주소 (FastAPI 주소)
    private static final String PYTHON_URL = "http://localhost:5000/analyze";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        // 1. 로그인 체크
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        if(userId == null) {
            resp.sendRedirect(req.getContextPath() + "/pointC/login/login.jsp");
            return;
        }

        HttpURLConnection conn = null;
        try {
            // 2. 파이썬 서버 연결 준비
            URL url = new URL(PYTHON_URL);
            conn = (HttpURLConnection) url.openConnection();
            
            // 통신 설정
            String boundary = "---PointC_Boundary_" + System.currentTimeMillis(); 
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // 3. 파일 전송 (JSP -> Java -> Python)
            try (OutputStream out = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {

                // JSP에서 넘겨준 파일들(front, back, left, right)을 하나씩 꺼내서 보냄
                for (Part part : req.getParts()) {
                    String name = part.getName();
                    // 우리가 원하는 4개 파일만 골라서 전송
                    if (name.equals("front") || name.equals("back") || name.equals("left") || name.equals("right")) {
                        if (part.getSize() > 0) { 
                            addFilePart(writer, out, name, part, boundary);
                        }
                    }
                }
                // 전송 끝 알림
                writer.append("--" + boundary + "--").append("\r\n");
                writer.flush();
            }

            // 4. 파이썬 응답 받기
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // 성공! 결과 읽기
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                String jsonResult = sb.toString();
                System.out.println("✅ 파이썬 분석 성공: " + jsonResult);

                // 5. JSON 파싱 및 DB 저장
                // (라이브러리 없이 간단히 문자열 처리로 값 추출)
                int score = parseJsonInt(jsonResult, "score");
                String badParts = parseJsonString(jsonResult, "bad_parts");
                String feedback = parseJsonString(jsonResult, "feedback");
                
                // DAO 호출해서 저장
                AnalysisDAO dao = new AnalysisDAO();
                AnalysisDTO dto = new AnalysisDTO();
                dto.setUserId(userId);
                dto.setScore(score);
                dto.setBadParts(badParts);
                dto.setFeedback(feedback);
                
                dao.insertResult(dto); // 저장!
                dao.close();

                // 6. 결과 페이지로 이동 (분석 내역 페이지)
                resp.sendRedirect(req.getContextPath() + "/analysisPage.do");
                
            } else {
                System.out.println("❌ 파이썬 서버 에러: " + responseCode);
                resp.sendRedirect(req.getContextPath() + "/pointC/error.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/pointC/error.jsp");
        } finally {
            if(conn != null) conn.disconnect();
        }
    }

    // [보조 함수 1] 파일 전송용 (건드리지 마세요)
    private void addFilePart(PrintWriter writer, OutputStream out, String fieldName, Part filePart, String boundary) throws IOException {
        String fileName = filePart.getSubmittedFileName();
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append("\r\n");
        writer.append("Content-Type: " + filePart.getContentType()).append("\r\n");
        writer.append("\r\n");
        writer.flush();

        InputStream inputStream = filePart.getInputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        inputStream.close();
        writer.append("\r\n");
        writer.flush();
    }
    
    // [보조 함수 2] JSON에서 숫자 꺼내기 (간단 파싱)
    private int parseJsonInt(String json, String key) {
        try {
            String search = "\"" + key + "\":";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Integer.parseInt(json.substring(start, end).trim());
        } catch(Exception e) { return 0; }
    }

    // [보조 함수 3] JSON에서 글자 꺼내기 (간단 파싱)
    private String parseJsonString(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search);
            if(start == -1) return "";
            start += search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch(Exception e) { return ""; }
    }
}