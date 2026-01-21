package pointC.log;

import java.util.ArrayList;
import java.util.List;
import common.JDBConnect;

public class LogDAO extends JDBConnect {

    public LogDAO() { super(); }

    // 1. 로그 저장하기 (알림 발생 시 호출)
    // 알림 기록 저장 (글자 저장용)
    public int insertLog(LogDTO dto) {
        int result = 0;
        String sql = "INSERT INTO posture_log (idx, user_id, message, type, regdate) "
                   + "VALUES (seq_posture_log.nextval, ?, ?, ?, sysdate)";
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, dto.getUserId());
            psmt.setString(2, dto.getMessage());
            psmt.setString(3, dto.getType());
            result = psmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 2. 최근 로그 목록 가져오기 (화면 로딩 시 호출)
    public List<LogDTO> selectRecentLogs(String userId) {
        List<LogDTO> list = new ArrayList<>();
        // 최근 10개만 가져오기
        String query = "SELECT * FROM (SELECT * FROM posture_log WHERE user_id=? ORDER BY idx DESC) WHERE ROWNUM <= 10";

        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, userId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                LogDTO dto = new LogDTO();
                dto.setMessage(rs.getString("message"));
                dto.setType(rs.getString("type"));

                // ★ [핵심] 그냥 rs.getDate() 하면 00:00이 됨.
                // 이렇게 Timestamp로 꺼내서 넣어야 "10:25" 처럼 시간이 나옴!
                java.sql.Timestamp ts = rs.getTimestamp("regdate");
                dto.setRegdate(new java.sql.Date(ts.getTime())); 
                
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
 // [NEW] 1. 그래프 데이터 저장 (SaveDataController에서 사용)
    public int insertSensorData(String userId, int score) {
        int result = 0;
        // 새로 만든 'sensor_data' 테이블에 넣습니다.
        String sql = "INSERT INTO sensor_data (idx, user_id, score, regdate) VALUES (seq_sensor_data.nextval, ?, ?, sysdate)";
        
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, userId);
            psmt.setInt(2, score);
            result = psmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // [NEW] 2. 그래프 데이터 조회 (AnalysisController에서 사용)
    public List<LogDTO> selectGraphData(String userId) {
        List<LogDTO> list = new ArrayList<>();
        // 쿼리문: 최근 20개 가져오기
        String sql = "SELECT * FROM (SELECT * FROM sensor_data WHERE user_id=? ORDER BY idx DESC) WHERE ROWNUM <= 20";
        
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, userId);
            
            // ★ 여기입니다! 쿼리 실행한 직후!
            rs = psmt.executeQuery(); 

            // [while문 위치] 결과가 있으면 계속 돌면서 리스트에 담는 역할
            while(rs.next()) { 
                LogDTO dto = new LogDTO();
                dto.setScore(rs.getInt("score"));
                dto.setRegdate(rs.getDate("regdate")); // 날짜 담기
                list.add(dto); // 리스트에 추가
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list; // 다 담은 리스트 반환
    
    }
}