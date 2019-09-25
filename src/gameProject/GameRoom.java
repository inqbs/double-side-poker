package gameProject;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import vo.ClientVO;

public class GameRoom implements Serializable{

    private int rooNum; // room index
    // 배팅과 관련된 정보
    private int prevBattingPoint; // 이전 배팅 정보. 판정 시 비교하기 위해서 필요
    private int cumulativeBattingPoint; // 누적 배팅 포인트 정보
    private DoubleSideCard deck[][] = new DoubleSideCard[10][10];
    private ArrayList<DoubleSideCard> suffledDeck = new ArrayList<>();
    private boolean turnCli1;
    private boolean turnCli2;
    // 채팅을 위한 8가지 멘트
    String chatMent[] = new String[8];
    // 이 방에 접속한 객체의 정보를 저장할 배열. 딱 두칸만 만들기
    private ClientVO client[] = new ClientVO[2];
    // 게임 시작 여부
    boolean startGame;
    

    //getter & setter
    public int getRooNum() {
        return rooNum;
    }

    public void setRooNum(int rooNum) {
        this.rooNum = rooNum;
    }

    public int getPrevBattingPoint() {
        return prevBattingPoint;
    }

    public void setPrevBattingPoint(int prevBattingPoint) {
        this.prevBattingPoint = prevBattingPoint;
    }

    public int getCumulativeBattingPoint() {
        return cumulativeBattingPoint;
    }

    public void setCumulativeBattingPoint(int cumulativeBattingPoint) {
        this.cumulativeBattingPoint = cumulativeBattingPoint;
    }

    public DoubleSideCard[][] getDeck() {
        return deck;
    }

    public void setDeck(DoubleSideCard[][] deck) {
        this.deck = deck;
    }

    public ArrayList<DoubleSideCard> getSuffledDeck() {
        return suffledDeck;
    }

    public void setSuffledDeck(ArrayList<DoubleSideCard> suffledDeck) {
        this.suffledDeck = suffledDeck;
    }

    public ClientVO[] getClient() {
        return client;
    }

    public void setClient(ClientVO[] client) {
        this.client = client;
    }

    public boolean isTurnCli1() {
        return turnCli1;
    }

    public void setTurnCli1(boolean turnCli1) {
        this.turnCli1 = turnCli1;
    }

    public boolean isTurnCli2() {
        return turnCli2;
    }

    public void setTurnCli2(boolean turnCli2) {
        this.turnCli2 = turnCli2;
    }

    

    public boolean isStartGame() {
		return startGame;
	}

	public void setStartGame(boolean startGame) {
		this.startGame = startGame;
	}

	//method
    public DoubleSideCard[][] cardGen(){
        int index = 1;

        //DoubleSideCard deck[][] = new DoubleSideCard[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(i == j) continue;
                else{
                    deck[i][j] = new DoubleSideCard(i+1,j+1,index++);
                }
            }
        }
        return deck;
    } // end cardGen method

    public void suffle(){
        HashSet<Integer> hs = new HashSet<>();
        Random rnd = new Random();
        while(hs.size() != 90){
            int a = rnd.nextInt(90)+1;
            hs.add(a);
        }

        Iterator<Integer> it = hs.iterator();
        while(it.hasNext()){
            int b = it.next();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if(i!=j){
                        if(deck[i][j].index == b){
                            suffledDeck.add(deck[i][j]);
                        }
                    }
                }
            }
        }
    } // end suffle method

    public DoubleSideCard pulledOut(){ // 카드를 한장씩 빼서 지급
        DoubleSideCard temp = suffledDeck.get(suffledDeck.size()-1);
        suffledDeck.remove(suffledDeck.size()-1);
        return temp;
    }

    public int judgeGame(String posC1, String posC2, DoubleSideCard cardC1, DoubleSideCard cardC2){
        // return 변수 구분
        /* 1 => 위 ClientVO[0]에 저장된 클라이언트의 승
         * 2 => 위 ClientVO[1]에 저장된 클라이언트의 승
         * 3 => 무승부
         * 4 => 판정에러
         * */

        String jPos = posC1.charAt(0) + posC2.charAt(0) + "";

        switch (jPos){
            case "ff":
                if(cardC1.front > cardC2.front){ return 1;
                }else if(cardC1.front == cardC2.front){ return 3;
                }else if(cardC1.front < cardC2.front){ return 2; }

                break;

            case "fb":
                if(cardC1.front > cardC2.back){ return 1;
                }else if(cardC1.front == cardC2.back){ return 3;
                }else if(cardC1.front < cardC2.back){ return 2; }

                break;

            case "fd":
            case "bd":
                if(cardC1.front > cardC2.front || cardC1.back > cardC2.back){ return 1;
                }else if(cardC1.front == cardC2.front && cardC1.back == cardC2.back){ return 3;
                }else if(cardC1.front < cardC2.front && cardC1.back < cardC2.back){ return 2; }

                break;

            case "bf":
                if(cardC1.back > cardC2.front){ return 1;
                }else if(cardC1.back == cardC2.front){ return 3;
                }else if(cardC1.back < cardC2.front){ return 2; }

                break;

            case "bb":
                if(cardC1.back > cardC2.back){ return 1;
                }else if(cardC1.back == cardC2.back){ return 3;
                }else if(cardC1.back < cardC2.back){ return 2; }

                break;

            case "df":
            case "db":
            case "dd":
                if(cardC1.front > cardC2.front && cardC1.back > cardC2.back){ return 1;
                }else if(cardC1.front == cardC2.front && cardC1.back == cardC2.back){ return 3;
                }else if(cardC1.front < cardC2.front && cardC1.back < cardC2.back){ return 2; }

                break;

//                default:
//
//                    break;
        }

        return 4; // 4는 판정 에러

    } // end judgeGame

    public int whosTurn(){
        // 누구의 턴인지 리턴해주는 매서드
        if(turnCli1 == true && turnCli2 == false){
            return 1;
        }else if(turnCli1 == false && turnCli2 == true){
            return 2;
        }else {
            return 3;
        }
    }

    private void chatInit(){
        chatMent[0] = "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ";
        chatMent[1] = "안녕하세요.";
        chatMent[2] = "감사합니다.";
        chatMent[3] = "쫄리면 되지시든가?"; // 원제 : 졸리면 주무시던가?
        chatMent[4] = "내가 빙다리 핫바지로 보이냐?"; // 원제 : 스겜 스겜
        chatMent[5] = "아모른직다";
        chatMent[6] = "나가지마라잉, 한판 더"; // 패배시
        chatMent[7] = "집팔아서 돈 더 가져와라잉"; // 승리시
    }

    // 클라이언트 정보를 보내주는 메서드
    public ClientVO returnCliData(ClientVO ownCli){

        ClientVO temp = null;
        for (int i = 0; i < client.length; i++) {
            if(client[i] != ownCli){
                temp = client[i];
            }
        }

        return temp;
    }

    // 선공 후공 결정
    public int setFirst(){

        Random rnd = new Random();

        int temp = rnd.nextInt(2)+1;

        return temp;
    }

    // 비어있는 클라이언트 번호 반환
    public int returnCliIndex(){
        int temp = 0;

        if(client[0] == null) temp = 1;
        else if(client[1] == null) temp = 2;
        else if(client[0] == null && client[1] == null)temp = 3; // 둘다 비었으면

        return temp;
    }
    // 자기자신의 인덱스를 반환
    public int returnOwnData(ClientVO ownCli) {
        int temp = -1;
        for (int i = 0; i < client.length; i++) {
            if(client[i].getUserCode() == ownCli.getUserCode() && client[i]!=null){
                temp = i;
            }
        }
        return temp;
    }

    //Constructor
    public GameRoom() {
        super();
        cumulativeBattingPoint = 0;
        prevBattingPoint = 0;
        cardGen();
        suffle();
        chatInit();
    }

}

