package pointC.todo;

import java.util.ArrayList;
import java.util.List;
import common.JDBConnect;

public class TodoDAO extends JDBConnect {
    
    public TodoDAO() {
        super();
    }
    
    // 기능: 로그인한 사용자의 투두 리스트만 가져오기
 // [수정] 특정 날짜의 투두 리스트만 가져오기 (dateStr: "YYYY-MM-DD")
    public List<TodoDTO> selectTodoList(String userId, String dateStr) {
        List<TodoDTO> list = new ArrayList<>();
        // postdate를 날짜까지만 잘라서(TRUNC) 비교
        String query = "SELECT * FROM todo_list WHERE user_id=? AND TO_CHAR(postdate, 'YYYY-MM-DD') = ? ORDER BY idx DESC";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, userId);
            psmt.setString(2, dateStr);
            rs = psmt.executeQuery();
            
            while(rs.next()) {
                TodoDTO dto = new TodoDTO();
                dto.setIdx(rs.getString("idx"));
                dto.setUserId(rs.getString("user_id"));
                dto.setContent(rs.getString("content"));
                dto.setIsCompleted(rs.getString("is_completed"));
                dto.setPostdate(rs.getDate("postdate"));
                list.add(dto);
            }
        } catch (Exception e) {
            System.out.println("날짜별 투두 조회 중 예외 발생");
            e.printStackTrace();
        }
        return list;
    }

    // [추가] 월별 달성률 통계 가져오기 (캘린더 색칠용)
    // 반환값: Map<날짜문자열, 성공레벨(0,1,2)>
    // 레벨 2: 100% 완료 (진한 파랑)
    // 레벨 1: 일부 완료 (연한 파랑)
    // 레벨 0: 미완료 or 없음 (회색)
    public java.util.Map<String, Integer> getMonthStats(String userId, String yearMonth) {
        // yearMonth 예: "2025-10"
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        
        // 날짜별로 [전체 개수]와 [완료된 개수]를 세는 쿼리
        String query = "SELECT TO_CHAR(postdate, 'YYYY-MM-DD') as d_day, "
                     + "COUNT(*) as total, "
                     + "SUM(CASE WHEN is_completed='1' THEN 1 ELSE 0 END) as done "
                     + "FROM todo_list "
                     + "WHERE user_id=? AND TO_CHAR(postdate, 'YYYY-MM') = ? "
                     + "GROUP BY TO_CHAR(postdate, 'YYYY-MM-DD')";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, userId);
            psmt.setString(2, yearMonth);
            rs = psmt.executeQuery();
            
            while(rs.next()) {
                String day = rs.getString("d_day");
                int total = rs.getInt("total");
                int done = rs.getInt("done");
                
                int level = 0;
                if (total > 0 && total == done) {
                    level = 2; // 완벽 달성
                } else if (done > 0) {
                    level = 1; // 일부 달성
                }
                stats.put(day, level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
    
 // ----------------------------------------------------
    // 기능 2. 할 일 추가 (insert)
    // ----------------------------------------------------
    public int insertTodo(TodoDTO dto) {
        int result = 0;
        // ✅ [수정] SYSDATE 대신 ?(물음표)를 하나 더 넣었습니다.
        String query = "INSERT INTO todo_list (idx, user_id, content, postdate) VALUES (seq_todo_num.nextval, ?, ?, ?)";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, dto.getUserId());
            psmt.setString(2, dto.getContent());
            psmt.setDate(3, dto.getPostdate()); // ✅ 날짜 데이터 세팅
            
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("투두 추가 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }

    // ----------------------------------------------------
    // 기능 3. 할 일 완료 토글 (0 <-> 1 변경)
    // ----------------------------------------------------
    public int toggleTodo(String idx) {
        int result = 0;
        // 현재 값이 '1'이면 '0'으로, '0'이면 '1'로 뒤집는 쿼리
        String query = "UPDATE todo_list SET is_completed = CASE WHEN is_completed='1' THEN '0' ELSE '1' END WHERE idx=?";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, idx);
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("투두 토글 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }

    // ----------------------------------------------------
    // 기능 4. 할 일 삭제 (delete)
    // ----------------------------------------------------
    public int deleteTodo(String idx) {
        int result = 0;
        String query = "DELETE FROM todo_list WHERE idx=?";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, idx);
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("투두 삭제 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }
}