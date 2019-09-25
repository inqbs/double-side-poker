package gameProject;

public class OrderMsg {
	
	//	recMsg에서 분류하기 위해
	public final int LOGIN = 0;	//	로그인
	public final int SIGNUP = 1;	//	회원가입
	
	
	
	public final int REFRESH_INFO = 2;	//	정보갱신	update all
	public final int ENTER_ROOM = 3;	//	방 입장
	public final int TRY_ROULETTE = 4;	//	룰렛 실행	update count, point
	
	//	게임 내
	
	public final int GAME_READY = 5;	//	게임 준비
	public final int BAT = 6;	//	배팅
	public final int CHAT = 7;	//	게임 내 채팅
	public final int EXIT_ROOM = 10; //	방 나가기
	
}
