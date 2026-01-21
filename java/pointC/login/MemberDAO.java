package pointC.login;

import common.JDBConnect;
import jakarta.servlet.ServletContext;

public class MemberDAO extends JDBConnect {

    public MemberDAO() { super(); }
    public MemberDAO(ServletContext application) { super(application); }

    // 기능 1. 회원가입
    public int insertMember(MemberDTO dto) {
        int result = 0;
        try {
            String query = "INSERT INTO member (id, pass, name) VALUES (?, ?, ?)";
            psmt = con.prepareStatement(query);
            psmt.setString(1, dto.getId());
            psmt.setString(2, dto.getPass());
            psmt.setString(3, dto.getName());
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("회원가입 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }

    // 기능 2. 로그인
    public MemberDTO getMember(String uid, String upw) {
        MemberDTO dto = new MemberDTO();
        String query = "SELECT * FROM member WHERE id=? AND pass=?";
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, uid);
            psmt.setString(2, upw);
            rs = psmt.executeQuery();
            if (rs.next()) {
                dto.setId(rs.getString("id"));
                dto.setPass(rs.getString("pass"));
                dto.setName(rs.getString("name"));
                dto.setRegidate(rs.getDate("regidate"));
            }
        } catch (Exception e) {
            System.out.println("로그인 처리 중 예외 발생");
            e.printStackTrace();
        }
        return dto;
    }
    
    // 기능 3. 회원 탈퇴
    public int deleteMember(String id) {
        int result = 0;
        String query = "DELETE FROM member WHERE id=?";
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, id);
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("회원 탈퇴 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }
    
    // ----------------------------------------------------
    // ✅ [수정됨] 기능 4. 내 정보 가져오기 (신체 정보 추가)
    // ----------------------------------------------------
    public MemberDTO getMemberInfo(String id) {
        MemberDTO dto = new MemberDTO();
        String query = "SELECT * FROM member WHERE id=?";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, id);
            rs = psmt.executeQuery();
            
            if (rs.next()) {
                dto.setId(rs.getString("id"));
                dto.setPass(rs.getString("pass"));
                dto.setName(rs.getString("name"));
                dto.setRegidate(rs.getDate("regidate"));
                
                // ▼ 여기가 추가된 부분입니다! (DB에서 꺼내서 DTO에 담기)
                dto.setHeight(rs.getDouble("height")); // 키
                dto.setWeight(rs.getDouble("weight")); // 몸무게
                dto.setGender(rs.getString("gender")); // 성별
            }
        } catch (Exception e) {
            System.out.println("정보 조회 중 예외 발생");
            e.printStackTrace();
        }
        return dto;
    }

    // ----------------------------------------------------
    // ✅ [수정됨] 기능 5. 회원 정보 수정 (신체 정보도 같이 수정)
    // ----------------------------------------------------
    public int updateMember(MemberDTO dto) {
        int result = 0;
        
        // ▼ 쿼리문에 height, weight, gender가 추가되었습니다.
        String query = "UPDATE member SET pass=?, name=?, height=?, weight=?, gender=? WHERE id=?";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, dto.getPass());   // 1번 물음표: 비번
            psmt.setString(2, dto.getName());   // 2번 물음표: 이름
            
            // ▼ 여기가 추가된 부분입니다! (순서 중요)
            psmt.setDouble(3, dto.getHeight()); // 3번 물음표: 키
            psmt.setDouble(4, dto.getWeight()); // 4번 물음표: 몸무게
            psmt.setString(5, dto.getGender()); // 5번 물음표: 성별
            
            psmt.setString(6, dto.getId());     // 6번 물음표: 아이디 (WHERE절)
            
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("정보 수정 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }
}