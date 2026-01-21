package pointC.login;

import java.sql.Date;

public class MemberDTO {
    // 1. 필드 (멤버 변수)
    private String id;       // 아이디
    private String pass;     // 비밀번호
    private String name;     // 이름
    private Date regidate;   // 가입일
    private double height;
    private double weight;
    public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	private String gender;
    
    // 2. 기본 생성자
    public MemberDTO() {
    	super();
    }
    
    // 3. 인자 생성자 (회원가입용)
    public MemberDTO(String id, String pass, String name) {
		super();
		this.id = id;
		this.pass = pass;
		this.name = name;
	}

	// 4. Getter / Setter 메서드
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Date getRegidate() { return regidate; }
    public void setRegidate(Date regidate) { this.regidate = regidate; }
}