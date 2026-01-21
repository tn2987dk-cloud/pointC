package pointC.todo;

import java.sql.Date;

public class TodoDTO {
    private String idx;
    private String userId;
    private String content;
    private String isCompleted; // "0":미완료, "1":완료
    private Date postdate;
    
    public TodoDTO() {}

    // Getter & Setter
    public String getIdx() { return idx; }
    public void setIdx(String idx) { this.idx = idx; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getIsCompleted() { return isCompleted; }
    public void setIsCompleted(String isCompleted) { this.isCompleted = isCompleted; }
    
    public Date getPostdate() { return postdate; }
    public void setPostdate(Date postdate) { this.postdate = postdate; }
}