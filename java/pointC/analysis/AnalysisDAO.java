package pointC.analysis;

import java.sql.*;
import java.util.*;
import common.JDBConnect; // DB연결 클래스 (기존에 쓰던거)

public class AnalysisDAO {
    private Connection con;
    private PreparedStatement psmt;
    private ResultSet rs;

    public AnalysisDAO() {
        try {
            JDBConnect jdbc = new JDBConnect(); 
            this.con = jdbc.con; // JDBConnect가 만든 연결을 내 변수에 담기
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 내 분석 기록 가져오기
    public List<AnalysisDTO> selectMyHistory(String userId) {
        List<AnalysisDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM analysis_result WHERE user_id = ? ORDER BY regdate DESC";
        
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, userId);
            rs = psmt.executeQuery();
            while(rs.next()) {
                AnalysisDTO dto = new AnalysisDTO();
                dto.setIdx(rs.getInt("idx"));
                dto.setScore(rs.getInt("score"));
                dto.setBadParts(rs.getString("bad_parts"));
                dto.setFeedback(rs.getString("feedback"));
                dto.setRegdate(rs.getDate("regdate"));
                list.add(dto);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public void close() { 
        try { if(rs!=null) rs.close(); if(psmt!=null) psmt.close(); if(con!=null) con.close(); } catch(Exception e){}
    }
    
    public int insertResult(AnalysisDTO dto) {
        int result = 0;
        String sql = "INSERT INTO analysis_result (idx, user_id, score, bad_parts, feedback, regdate) "
                   + "VALUES (seq_analysis_result.nextval, ?, ?, ?, ?, sysdate)";
        
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, dto.getUserId());
            psmt.setInt(2, dto.getScore());
            psmt.setString(3, dto.getBadParts());
            psmt.setString(4, dto.getFeedback());
            
            result = psmt.executeUpdate(); // 실행!
            
        } catch (Exception e) {
            System.out.println("분석 결과 저장 실패!");
            e.printStackTrace();
        }
        return result;
    }
}