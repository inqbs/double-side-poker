package vo;

public class BKVO {
	String Id;
	String Pw;
	String Name;
	String Email;
	String phone;
	
	public BKVO() {}
	
	public BKVO(String id, String pw, String name, String email, String phone) {
		super();
		Id = id;
		Pw = pw; 
		Name = name;
		Email = email;
		this.phone = phone;
	}

	public String getId() {
		return Id;
	}
	
	public void setId(String id) {
		Id = id;
	}
	
	public String getPw() {
		return Pw;
	}
	
	public void setPw(String pw) {
		Pw = pw;
	}
	
	public String getName() {
		return Name;
	}
	
	public void setName(String name) {
		Name = name;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public void setEmail(String email) {
		Email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
