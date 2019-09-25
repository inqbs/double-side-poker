package vo;


import java.io.Serializable;

import gameProject.DoubleSideCard;
import gameProject.GameRoom;

public class ClientVO implements Serializable{
	
	int UserCode;
	String id, pw, email, phone;
	String nickName;
	int level, exp;
	int point, counter;
	int enterRoomNo;
	
	
	
//	userCode
//	id
//	nickname
//	level
//	exp
//	point
//	counter
//	enterRoomNo	//	들어간 방 번호 (안들어 갔다면 0)
	
	
	public int getUserCode() {
		return UserCode;
	}
	public void setUserCode(int userCode) {
		UserCode = userCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getEnterRoomNo() {
		return enterRoomNo;
	}
	public void setEnterRoomNo(int enterRoomNo) {
		this.enterRoomNo = enterRoomNo;
	}

	
	
}