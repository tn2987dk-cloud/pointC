package pointC.log;

import java.sql.Date; // SQL Date를 써야 DB랑 호환됩니다!

public class LogDTO {
    // DB 컬럼 이름과 매칭되는 필드들
    private String idx;      // 글 번호
    private String userId;   // 사용자 ID
    private int score;       // 점수
    private String message;  // 내용
    private String type;     // 타입 (INFO/WARN)
    private Date regdate;    // 등록일 (String time 대신 Date 사용)
    private String indate;

    public String getIndate() {
		return indate;
	}

	public void setIndate(String indate) {
		this.indate = indate;
	}

	// 1. 기본 생성자 (필수! DAO에서 객체 생성할 때 씀)
    public LogDTO() {}

    // 2. 모든 필드 생성자 (편의용)
    public LogDTO(String idx, String userId, int score, String message, String type, Date regdate) {
        this.idx = idx;
        this.userId = userId;
        this.score = score;
        this.message = message;
        this.type = type;
        this.regdate = regdate;
    }

    // 3. Getter & Setter (DAO가 데이터를 넣고 뺄 때 씀)
    public String getIdx() { return idx; }
    public void setIdx(String idx) { this.idx = idx; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Date getRegdate() { return regdate; }
    public void setRegdate(Date regdate) { this.regdate = regdate; }
}