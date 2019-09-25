package vo;

import java.io.Serializable;
import java.util.ArrayList;

import gameProject.DoubleSideCard;

public class RoomVO implements Serializable{
	int roomNo;
	boolean playing;
	String p1UserCode, p2UserCode;
	String p1UserName, p2UserName;
	boolean p1Ready;
	boolean p2Ready;
	
	
	//	현재 보유 칩
	int p1Chips;
	int p2Chips;
	
	//	배팅 패널에 배팅한 칩
	int centerBetChips;
	int p1BetChips;
	int p2BetChips;

	int p1FrontBack;
	int p2FrontBack;
	
	
	private ArrayList<DoubleSideCard> suffledDeck = new ArrayList<>();

//	  * RoomVO
//	  roomNo = (룸 번호);
//	  playing = (게임진행여부);
//	  p1UserCode = (p1 유저코드);
//	  p2UserCode = (p2 유저코드);
//	  p1Ready = (p1 준비여부 / p1 턴 여부);	//	playing False / true;
//	  p2Ready = (p2 준비여부 / p2 턴 여부);
//	  betchips = (공통배팅칩);
//	  p1frontBack = (f/b/a 장소 | 추가/콜/다이);	//	p2chips<=1 첫 턴 / 이후 
//	  p2frontBack = (f/b/a 장소 | 추가/콜/다이);	//	p1chips<=1 첫 턴 / 이후
//	  p1chips = (p1배팅칩);
//	  p2chips = (p2배팅칩);
//	  cardDeck = (카드 덱);
	
	public int getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(int roomNo) {
		this.roomNo = roomNo;
	}

	public boolean isplaying() {
		return playing;
	}

	public void setplaying(boolean playing) {
		this.playing = playing;
	}

	public String getP1UserCode() {
		return p1UserCode;
	}

	public void setP1UserCode(String p1UserCode) {
		this.p1UserCode = p1UserCode;
	}

	public String getP2UserCode() {
		return p2UserCode;
	}

	public void setP2UserCode(String p2UserCode) {
		this.p2UserCode = p2UserCode;
	}

	public String getP1UserName() {
		return p1UserName;
	}

	public void setP1UserName(String p1UserName) {
		this.p1UserName = p1UserName;
	}

	public String getP2UserName() {
		return p2UserName;
	}

	public void setP2UserName(String p2UserName) {
		this.p2UserName = p2UserName;
	}

	public boolean isP1Ready() {
		return p1Ready;
	}

	public void setP1Ready(boolean p1Ready) {
		this.p1Ready = p1Ready;
	}

	public boolean isP2Ready() {
		return p2Ready;
	}

	public void setP2Ready(boolean p2Ready) {
		this.p2Ready = p2Ready;
	}

	public int getCenterBetChips() {
		return centerBetChips;
	}

	public void setCenterBetChips(int centerBetChips) {
		this.centerBetChips = centerBetChips;
	}

	public int getP1BetChips() {
		return p1BetChips;
	}

	public void setP1BetChips(int p1BetChips) {
		this.p1BetChips = p1BetChips;
	}

	public int getP2BetChips() {
		return p2BetChips;
	}

	public void setP2BetChips(int p2BetChips) {
		this.p2BetChips = p2BetChips;
	}

	public int getP1FrontBack() {
		return p1FrontBack;
	}

	public void setP1FrontBack(int p1FrontBack) {
		this.p1FrontBack = p1FrontBack;
	}

	public int getP2FrontBack() {
		return p2FrontBack;
	}

	public void setP2FrontBack(int p2FrontBack) {
		this.p2FrontBack = p2FrontBack;
	}

	public int getP1Chips() {
		return p1Chips;
	}

	public void setP1Chips(int p1Chips) {
		this.p1Chips = p1Chips;
	}

	public int getP2Chips() {
		return p2Chips;
	}

	public void setP2Chips(int p2Chips) {
		this.p2Chips = p2Chips;
	}
	
	
	
}
