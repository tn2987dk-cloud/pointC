package pointC.analysis;

import java.sql.Date;

public class AnalysisDTO {
	private int idx;
    private String userId;
    private int score;
    private String badParts;
    private String feedback;
    private Date regdate;
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getBadParts() {
		return badParts;
	}
	public void setBadParts(String badParts) {
		this.badParts = badParts;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
}
