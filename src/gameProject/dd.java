//[서/클 동기화 방식]
//VO 객체를 서버 클라이언트가 모두 가지고 있으면서 수정 내용을 서로가 동기화 하는 방식
//(클라가 변경 request를 하면, 서버단에서 변경로직을 수행해서 수정된 VO를 전송해서 반영하는 방식)
// * 모든 요청은 usercode가 필요하다.
//
// * 명령어 모음
//로그인:		/LOGIN	ID, PW	-> return ClientVO
//아디체크:	/CHECK	ID	->	return "/CHECK + boolean"
//계정생성:	/SGNUP	ID, PW, NAME, EMAIL, PHONE -> return "/SGNUP + boolean"
//
//정보갱신:	/CLREF	usercode -> return ClientVO
//로비대화:	/COMNT	"args" 	-> broadcast() -> return "/COMNT" + String
//방입장:		/ENTER	usercode -> return "/ENTER "+ roomCode
//
//룰렛실행:	/RUNRL	usercode, point, count	->	return "/RUNRL + cnt"
//
//(방입장후)
// 정보갱신:	/ROORE	roomCode	-> return RoomVO
// 룸안대화:	/RMCOM	usercode, commentIndex	-> return "/RMCOM" + usercode, commentIndex
// 준비완료:	/READY	userCode	->	return RoomVO
//(게임관련) 
// 배팅확인:	/BETGO	usercode, f/b/a, chips	->	return RoomVO
// 
//
//  * RoomVO
// roomNo = (룸 번호);
// Played = (게임진행여부);
// p1UserCode = (p1 유저코드);
// p2UserCode = (p2 유저코드);
// p1Ready = (p1 준비여부 / p1 턴 여부);	//	played False / true;
// p2Ready = (p2 준비여부 / p2 턴 여부);
// betchips = (공통배팅칩);
// p1frontBack = (f/b/a 장소 | 추가/콜/다이);	//	p2chips<=1 첫 턴 / 이후 
// p2frontBack = (f/b/a 장소 | 추가/콜/다이);	//	p1chips<=1 첫 턴 / 이후
// p1chips = (p1배팅칩);
// p2chips = (p2배팅칩);
// cardDeck = (카드 덱);
// 
//  * 각 명령어별 실행 과정
// cli:	/명령어	데이터:	
//	->	serv:	String에서 명령어와 데이터를 분리, 명령어로 Switch해서 아래 로직들을 실행
//	->	cli: (VO) 클라 안 해당 vo 갱신	(String) /명령어 결과값 split후 처리 (Int) 게임내 대화
// 
// 
// cli: /LOGIN ID,PW 
//	-> serv: ID,PW를 split, dao.login(id, pw) => return if(ClientVO != null) ClientVO, null 
//	-> cli: if(ClientVO!=null) new Lobby(vo, ois, oos), "로그인 실패";
// 
// cli: /CHECK ID	
//	-> serv:	dao.checkID(id) => return "/checkid"+if(boolean) true, false 
//	-> cli: if(boolean) "사용가능합니다.", "사용중인 아디입니다.";
// 
// cli: /SGNUP ID, PW, NAME, EMAIL, PHONE 
//	-> serv: 각 요소를 split후 임시 VO에 저장, dao.signUp(tmp) => return "/signup"+if(boolean) true, false // 이부분 dao return을 boolean 수정
//	-> cli: if(boolean) "처리완료", "에러";
//	
// cli: /CLREF usercode
//	-> serv: dao.refreshUser(usercode), => return ClientVO
//	-> cli:	VO 갱신후 라벨내 정보 재 정의
//	
// cli: /COMNT String ""
//	-> serv: broadcast() => return "/comment" + String
//	-> cli: ("/comment" 제거후) String을 메시지 창에 append
//	
// cli: /RUNRL usercode, point, count
//	-> serv: dao.updatePoint(usercode, point, count) => return "/runrullet" + cnt
//	-> cli: if(boolean) point, count 갱신, "에러";
//	
// cli: /ENTER usercode
//	-> serv: <대충 방 있으면 들어가고, 없으면 방생성하고 거기에 넣는 코드> => return roomCode
//	-> cli: new Room(usercode, name, point, roomCode, oos, ois), "에러"
//
// cli: /ROORE roomCode
//	-> serv: <대충 방목록에서 룸코드 방을 찾는 코드> => (if null이 아닌 p1 p2에게) return RoomVO
//	-> cli: VO 갱신후 라벨, 패널 등의 정보 재정의 
//			if(p1의 배팅액, p2의 배팅액이 모두 존재한다면) front, back, .. 버튼을 추가배팅, 콜, 다이로 변경
//	
// cli: /RMCOM commentIndex, usercode
//	-> serv: <대충 방안에 있는 사용자에게> => return usercode commentIndex
//	-> cli: usercode가 p1, p2의 어디에 있는지 판단하고, 거기에 commentIndex를 이용해 대화 표시
//	
// cli: /READY userCode
//	-> serv: usercode의 상태를 ready로 변경 => return RoomVO
//	-> cli: VO 갱신후 라벨, 패널 등의 정보 재정의
//
// cli: /BETGO usercode, f/b/a, chips
//	-> serv: f/b/a와 chips를 split해서, usercode의 f/b/에 해당 chips를 set => return RoomVO
//	-> cli: VO 갱신후 라벨, 패널 등의 정보 재정의
// 
// 
// * 배팅 로직
// 1턴 (선공): 기본배팅존에 칩이 있고, 자신 및 상대방의 f/b에 아무 칩도 없는 상태
//	f/b/a 배팅장소를 결정하고, 배팅액을 BetValSpn.getValue로 get하여, /betgo usercode, f/b/a, chips
// 
// 1턴 (후공): 자신에는 아무 칩이 없으나 상대방에는 칩이 있는 상태
//	f/b/a 배팅장소를 결정하고, 배팅액을 BetValSpn.getValue로 get하여,
//		if(value > 선공의 배팅칩) /betgo usercode, f/b/a, chips
//		else 다시 선택
// 
// 2턴 이후 : 자신 및 상대방에 칩이 있는 상태
//	f/b/a 추가배팅/콜/다이를 결정하고, 배팅액을 BetValSpn.getValue로 get하여 /betgo usercode, f/b/a, chips
// 
// 
// 
// 
// 
// 
// 

//package gameProject;
//
//public class dd{
//	public static void main(String[] args) {
//		
//		int casa = 1;
//		while(true) {
//			switch(casa) {
//			case 1:
//				break;
//			case 2:
//				break;
//			}
//		}
//		
//	}
//}