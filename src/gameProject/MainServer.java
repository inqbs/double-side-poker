package gameProject;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import dao.UserDAO;
import vo.ClientVO;
import vo.RoomVO;

public class MainServer {

    ServerSocket ss;

    ArrayList<EServer> list = new ArrayList<>();

    int gameRoomIndex;

    ArrayList<RoomVO> gameRoomList = new ArrayList<>();

    // Constructor
    MainServer() {
        this.gameRoomIndex = 1;
        startServer();
    }

    // main method
    public static void main(String[] args) {
        MainServer mgs = new MainServer();
    }

    private void startServer() {
        System.out.println("startServer server start");
        try {
        	ss = new ServerSocket(5000);
        	
        	while(true) {
	            Socket client = ss.accept();
	            
	            InetAddress inet = client.getInetAddress();
				System.out.println(inet.getHostAddress());
	            
	            EServer s = new EServer(client);
	            list.add(s);
	            s.start();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // inner Class
    class EServer extends Thread {
        Socket client;
        String ip;
        RoomVO room;
        ClientVO c;

        // IO
        ObjectOutputStream oos;
        ObjectInputStream ois;

        EServer(Socket client) {
            this.client = client;
            ip = client.getInetAddress().getHostAddress();

            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            UserDAO dao = new UserDAO();
            
            while (true) {
                try {
                    String readString = ois.readObject().toString().trim();
                    System.out.println("CLient가 요청한 메시지" + readString);
                    String order = readString.substring(1, 6);	//	명령어
                    String value = readString.substring(7);	//	값
                    
                    switch(order) {
                    case "LOGIN":
                    	//	로그인 명령
                    	String[] valueList = value.split(",",0); //	value 분할
                    	
                    	c = dao.login(valueList[0], valueList[1]);
                    	
                    	oos.writeObject(c);	//	dao실행 후 결과값 클라이언트로
                    	oos.reset();
                    	break;
                    case "CHECK":
                    	//	id체크 명령
                    	String sendCmd = "/"+ order + " " + dao.selectIdCheck(value.trim());	//	dao 실행후
                    	oos.writeObject(sendCmd);	//	결과값 클라이언트로
                    	oos.reset();
                    	break;
                    case "SGNUP":
                    	//	계정생성 명령
                    	valueList = value.split(",", 0);
                    	
                    	ClientVO tmpVo = new ClientVO();
                    	tmpVo.setId(valueList[0]);
                    	tmpVo.setPw(valueList[1]);
                    	tmpVo.setNickName(valueList[2]);
                    	tmpVo.setEmail(valueList[3]);
                    	tmpVo.setPhone(valueList[4]);
                    	
                    	sendCmd = "/"+ order + " " + dao.registratorUser(tmpVo);
                    	oos.writeObject(sendCmd);
                    	oos.reset();
                    	break;
                    case "CLREF":
                    	//	(로비의 정보갱신을 위한) 클라이언트 정보갱신
                    	tmpVo = dao.refresh(Integer.parseInt(value));
                    	
                    	//	받아온 레벨, 경험치, 포인트, 카운트 정보를 
                    	//	서버의 ClientVO에 설정하고
                    	c.setLevel(tmpVo.getLevel());
                    	c.setExp(tmpVo.getExp());
                    	c.setPoint(tmpVo.getPoint());
                    	c.setCounter(tmpVo.getCounter());
                    	
                    	//	설정한 vo를 클라이언트로 넘긴다.
                    	oos.writeObject(c);
                    	oos.reset();
                    	break;
                    case "COMNT":
                    	//	로비내 대화
                    	broadcast(value);
                    	break;
                    case "ENTER":
                    	//	방 입장 코드
                    	//	value :  usercode;
                    	while(true) {
                    		System.out.println("방 번호는 " + gameRoomIndex);
                    		RoomVO thisGameRoom = null;
                    		if(gameRoomList.size() == 0) {
                    			//	전체 방 목록에 방이 없음
                    			createRoom(gameRoomIndex);
                    		}
                    		//	 방 목록에 방이 있음
//                    		else if((thisGameRoom = gameRoomList.get(gameRoomList.size()-1)).getRoomNo() == c.getEnterRoomNo()){
//                    			oos.writeObject("/" +order + " " + gameRoomIndex);	//	룸번호 전송
//                    			oos.reset();
                    		else if((thisGameRoom = gameRoomList.get(gameRoomList.size()-1)).getP1UserCode() == null) {
                    			//	방의 p1이 없을 때
                    			System.out.println(c.getNickName()+"이 p1으로 로그인 합니다.");
                    			thisGameRoom.setP1UserCode(value); //	방에 P1 유저코드 저장
                    			thisGameRoom.setP1UserName(c.getNickName());
                    			thisGameRoom.setP1Chips(c.getPoint());
                    			oos.writeObject("/" +order + " " + gameRoomIndex);	//	룸번호 전송
                    			c.setEnterRoomNo(gameRoomIndex);	//	서버안 ClientVO에 룸번호 저장
                    			oos.reset();
                    			
                    			room = thisGameRoom;
                    			
                    			break;
                    		}else if(thisGameRoom.getP2UserCode() == null) {
                    			//	방의 p2가 없을 때
                    			System.out.println(c.getNickName()+"이 p2로 로그인 합니다.");
                    			System.out.println(thisGameRoom.getP1UserName() + "이 접속중");
                    			thisGameRoom.setP2UserCode(value); //	방에 P1 유저코드 저장
                    			thisGameRoom.setP2UserName(c.getNickName());
                    			thisGameRoom.setP2Chips(c.getPoint());
                    			oos.writeObject("/" +order + " " + gameRoomIndex);	//	룸번호 전송
                    			c.setEnterRoomNo(gameRoomIndex);	//	서버안 ClientVO에 룸번호 저장
                    			oos.reset();
                    			
                    			room = thisGameRoom;
                    			break;
                    		}
                    		
                    	}
                    	break;
                    case "RUNRL":
                    	//	룰렛 실행 코드
                    	System.out.println("룰렛실행");
                    	valueList = value.split(",", 0);
	                    oos.writeObject( "/"+ order + " " +dao.updatepoint(Integer.parseInt(valueList[0]),
	                    		Integer.parseInt(valueList[1]),
	                    		Integer.parseInt(valueList[2])));
	                    oos.reset();
                    	break;
                    case "READY":
                    	//	게임 시작 준비완료
//                    	System.out.println(room);
                    	if(room.getP1UserCode().equals(value)) {
                    		if(room.isP1Ready() == false) room.setP1Ready(true);
                    		else room.setP1Ready(false);
                    	}else if (room.getP2UserCode().equals(value)){
                    		if(room.isP2Ready() == false) room.setP2Ready(true);
                    		else room.setP2Ready(false);
                    	}
//                    	//	그대로 정보 갱신 하므로 break 없이 룸정보 갱신 코드로
                    	value = gameRoomIndex+"";
                    	
                    case "ROORE":
                    	//	룸 정보 갱신 코드 (value: roomNum)
                    	
                    	System.out.println(c.getEnterRoomNo());
                    	System.out.println(value);
                    	
                    	for (EServer x : list) {
                    		//	전체 서버에 접속중인 방에서 client가 요청한 방번호의 방에 들어간 모든 사람에게
                    		if(x.c!=null && x.c.getEnterRoomNo() == Integer.parseInt(value)) {
                    			for(RoomVO y : gameRoomList) {
                    				//	방 전체 목록에서 그 방을 찾아
                            		if(y.getRoomNo() == Integer.parseInt(value)) {
                            			x.oos.writeObject(y);
                            			x.oos.reset();
                            			
                            			if(y.isP1Ready()&&y.isP2Ready()) {
                            				x.oos.writeObject("/ALERT 잠시후 게임이 시작됩니다.");
                            				x.oos.reset();
                            				
                            				//	5초후 게임 시작
                            				TimerTask task = new TimerTask() {
                            					@Override
                            					public void run() {
                            						try {
                            							y.setplaying(true);
														x.oos.writeObject(y);
														x.oos.reset();
														x.oos.writeObject("/ALERT 게임 시작!");
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
                            					}
                            				};
                            				new Timer().schedule(task, 5000);
                            			}
                            			room = y;
                            			//	그 방을 전송
                            			break;
                            		}
                            	}
                    		}
                    	}
                    		
                    	break;
                    	
                    case "RMCOM":
                    	//	룸안 대화 로직
                    	valueList = value.split(",", 0);
                    	int tmp = Integer.parseInt(valueList[0]);
                    	for (EServer x : list) {
                    		//	전체 서버에 접속중인 방에서 client가 요청한 방번호의 방에 들어간 모든 사람에게
                    		if(x.c.getEnterRoomNo() == tmp) {
                    			for(RoomVO y : gameRoomList) {
                    				//	방 전체 목록에서 그 방을 찾아
                            		if(y.getRoomNo() == tmp) {
                            			x.oos.writeObject("/"+order+" " + valueList[1] +","+ valueList[2]);
                            			//	그 방을 전송
                            			x.oos.reset();
                            			break;
                            		}
                            	}
                    		}
                    	}
                    	break;
                    case "BETGO":
                    	//	배팅 완료
                    	
                    	
                    	break;
                    }
                    
//                    if (obj instanceof ClientVO) {
//
//                        c = (ClientVO) obj;
//                        System.out.println("MainServer VO read=========================================================");
//                        System.out.println("Main run에서 전달 받은 obj" + obj);
//                        System.out.println("Main run에서 저장 후 client" + c);
//                        System.out.println("일단 위에 두줄이 같은지 확인");
//
//                        String recMsg = c.getOrderMsg();
//                        System.out.println(recMsg);
//
//                        ClientVO uvo = null;
//                        UserDAO dao = null;
//
//                        switch (recMsg) {
//                            // 로그인 관련 case
//                            case "login":
//                                System.out.println("MainServer.java/InnerClass EServer switch - case => Client.java로 전송");
//                                uvo = new ClientVO();
//                                dao = new UserDAO();
//                                String id = c.getId();
//                                String pw = c.getPw();
//
//                                uvo = dao.login(id, pw);
//
//                                if (uvo != null) {
//                                    c.setUserCode(uvo.getUserCode());
//                                    c.setNickName(uvo.getNickName());
//                                    c.setId(uvo.getId());
//                                    c.setLevel(uvo.getLevel());
//                                    c.setExp(uvo.getExp());
//                                    c.setPoint(uvo.getPoint());
//                                    c.setCounter(uvo.getCounter());
//                                    c.setLoginState(true);
//                                    c.setOrderMsg("loginSuccess");
//
//                                }else {
//                                	c.setOrderMsg("loginFail");
//                                }
//                                System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                sendGameClient(c);
//
//                                break;
//
//                            case "SignUp":
//                                System.out.println("MainServer.java/InnerClass EServer switch - case => Client.java로 전송");
//                                dao = new UserDAO();
//                                dao.registratorUser(c);
//                                c.setOrderMsg("signUpPopIpExit");
//
//                                System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                sendGameClient(c);
//                                break;
//
//                            case "checkId":
//                                System.out.println("MainServer.java/InnerClass EServer switch - case => Client.java로 전송");
//                                dao = new UserDAO();
//                                System.out.println(c.getId());
//                                boolean isDup = dao.selectIdCheck(c.getId());
//
//                                if (!isDup) {
//                                    System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                    c.setOrderMsg("isDuplicate");
//                                    sendGameClient(c);
//
//                                } else {
//                                    System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                    c.setOrderMsg("isNotDuplicate");
//                                    sendGameClient(c);
//                                }
//
//                                break;
//
//                            case "popExit":
//                                System.out.println("MainServer.java/InnerClass EServer switch - case => Client.java로 전송");
//                                c.setOrderMsg("PopIpExit");
//
//                                System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                sendGameClient(c);
//                                break;
//
//                            case "refresh":
//                                System.out.println("refresh Practice");
//                                dao = new UserDAO();
//                                dao.refresh(c);
//
//                                System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                sendGameClient(c);
//                                // lobby에서 일어나는 명령
//                                break;
//
//                            case "createRoom": //이게 lobby로 갈까?
//                                System.out.println("MainServer.java/InnerClass EServer switch - case => Client.java로 전송");
//                                System.out.println("create Room에서 받는 객체 정보 : " + c);
//                                System.out.println("VO의 객체 정보를 확인 : "+c.getOrderMsg() + "," + c.getId() + "," + c.getNickName() + "," + c.getPoint());
//                                System.out.println("createRoom을 시작합니다.");
//                                // 1. 방의 정보를 저장할 참조변수 생성
//                                r = new GameRoom();
//                                // 2. 클라이언트의 방 참여 정보 확인
//                                while (!c.isEnterRoom()){
//                                    System.out.println("클라이언트가 방에 들어가지 않았습니다.");
//
//                                    if(gameRoomList.size() == 0) {
//                                        System.out.println("현재 ArayList에 방이 없습니다.");
//                                        creatRoom(gameRoomIndex);
//                                    }else {
//                                        System.out.println("방은 있는데, 클라이언트가 들어가지 않은 상황");
//                                        if(gameRoomList.get(gameRoomList.size()-1).getClient()[0] == null || gameRoomList.get(gameRoomList.size()-1).getClient()[1] == null){
//                                            r = gameRoomList.get(gameRoomList.size()-1);
//                                            c.setEnterRoom(true);
//                                            c.setRoomIndex(r.getRooNum());
//                                            // =============== r안에 client 정보를 일단 저장 =================
//                                            // 방에 클라이언트 정보 2칸중에 첫번째 칸이 비었으면 (혹은 둘다 비었다면 ) 첫번째 칸에 저장
//                                            if(r.returnCliIndex() == 1 || r.returnCliIndex() == 3) {
//                                                r.getClient()[0] = c;
//                                                System.out.println("클라정보 : " + c);
//                                                System.out.println("클라정보 저장되는 룸 안에 : " + r.getClient()[0]);
//                                            }else { // 첫번째칸은 차있고, 두번째 칸이 비었다면 두번째 칸에 채운다.
//                                                r.getClient()[1] = c;
//                                            }
//                                        }else{
//                                            creatRoom(gameRoomIndex);
//                                        }
//                                    }
//                                }
//
//                                c.setOrderMsg("doneCreateRoom");
//                                sendGameClient(c);
//                                break;
//
//                            // room에서 일어나는 명령
//                            case "reqGameRoom":
//                                //int tempRoomIndex = c.
//                                // 방에 대한 정보를 보낸다.
//                                System.out.println("룸 정보 요청 들어옴");
//                                for(GameRoom x : gameRoomList) {
//                                    if(x.getRooNum() == c.getRoomIndex()) {
//                                        System.out.println("클라이언트가 속한 방정보  :"+x);
//                                        sendRoom(x);
//                                        System.out.println("x방의 1 번째 클라이언트 :"+x.getClient()[0]);
//                                        System.out.println("x방의 2 번째 클라이언트 :"+x.getClient()[1]);
//                                        
//                                        
//                                        System.out.println("x방에 들어가 있어야되는 클라이언트 : "+c);
//                                    }
//                                }
//
//                                break;
//                            case "updatePoint":
//                                System.out.println(c.getUserCode()+ ","+ c.getPoint() + "," + c.getCounter());
//                                dao = new UserDAO();
//                                dao.updatepoint(c.getUserCode(),c.getPoint(),c.getCounter());
//                            	break;
//                        }
//                        recMsg = null;
//                        dao = null;
//                        uvo = null;
//                        System.out.println("run method에서 while문이 한바퀴 돌았습니다. Message null");
//
//                    } else if (obj instanceof String) {
//                        System.out.println("String 객체를 받았습니다. Lobby chat입니다.");
//                        String msg = (String) obj;
//                        broadcast(msg);
//                        System.out.println(msg);
//
//                    } else if (obj instanceof Integer) {
//                        System.out.println("Integer 객체를 받았습니다. game chat입니다.");
//
//                    }else if(obj instanceof GameRoom) {
//                        System.out.println("GameRoom객체를 받았습니다.");
//                        GameRoom temp = null;
//                        temp = (GameRoom) obj;
//                        r = temp;
//                        // 명령에 필요한 정보를 가져옵니다.
//                        boolean bothReady = false; // <== 모든 게임의 진행은 both ready가 true이어야 함
//                        if(bothReady == false && temp.getClient()[0].isReadyState() == true && temp.getClient()[1].isReadyState() == true) {
//                        	System.out.println("게임 스타트");
//                        	bothReady = true;
//                        	r.setStartGame(true); // 방이 게임이 시작되었다는 sign
//                        	// 카드 뽑아서 전달.
//                        	for(int i = 0; i < 2; i++) { // 두명의 클라이언트에게 카드가 돌아 감
//                        		if(r.getSuffledDeck().size() < 2) { // 카드가 뿌릴 장수보다 적으면
//                        			r.cardGen();
//                        			r.suffle();
//                        			i = 0;
//                        		}else {
//                        			DoubleSideCard tempCard = r.pulledOut();
//                        			r.getClient()[i].setPulledOutDeck(tempCard);
//                        			tempCard = null;                        			
//                        		}
//                        	} // end pulledOutCard
//                        	// 선공 후공 결정
//                        	int whosFirst = r.setFirst(); // 1이면 클라 0 2면 클라 1
//                        	if(whosFirst == 1) {
//                        		r.setTurnCli1(true);
//                        		r.setTurnCli2(false);
//                        	}else if(whosFirst == 2) {
//                        		r.setTurnCli1(false);
//                        		r.setTurnCli2(true); 
//                        	}
//                        	// 초기 배팅 <= lobby에서 코인이 10개 이하면 입장불가
//                        	r.setCumulativeBattingPoint(2);
//                        	// 각각 1개씩 감소하고, 위에 두개 증가하는 구조
//                        	r.getClient()[0].setPoint(r.getClient()[0].getPoint()-1);
//                        	r.getClient()[1].setPoint(r.getClient()[1].getPoint()-1);
//                        	
//                        }else if(bothReady == false && (temp.getClient()[0].isReadyState() == true || temp.getClient()[1].isReadyState() == true)){// 둘다 ready가 아니니까 대기하라는 메시지를 준다. ( 사실상 이부분은, 
//                        	// 사실상 room이 read를 하기위해 thread가 돌고 있으므로, 아무 명령도 주지 않으면 계속 read를 하기 위해 돌고 있을 것
//                        }else if(bothReady == true && r.getClient()[0].isMyTurn() == true && r.getClient()[1].isMyTurn() == false) {// 클라이언트[0] 차례
//                        	System.out.println("클라이언트 1 입니다.");
//                        }else if(bothReady == true && r.getClient()[0].isMyTurn() == false && r.getClient()[1].isMyTurn() == true) {// 클라리언트[1] 차례
//                        	System.out.println("클라이언트 2 입니다.");
//                        }
//                      
//                        // 게임 시작 세팅이 모두 정리가 되었으면
//                        sendRoom(r);
                       
//                    }else if (obj == null) {
//                        System.out.println("instance classification error");
//                    }

                } catch (SocketException e) {
                	list.remove(this);
                	
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } // while end
        }// run method end

//        public void sendGameClient(ClientVO pd) {
//            ClientVO sendData = pd;
//            System.out.println("send method에서 보내는 VO의 객체 정보를 확인 : "+sendData.getOrderMsg() + "," + sendData.getId() + "," + sendData.getNickName() + "," + sendData.getPoint());
//            try {
//                oos.writeUnshared(sendData);
//                System.out.println("Client send");
//                oos.reset();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        
        //	메시지 전체로 전송
        private void broadcast(String msg) {
            for (EServer x : list) {
                try {
                    x.oos.writeObject("/COMNT "+msg);
                    System.out.println(oos);
                    System.out.println("broadCast 메소드로 전달된 메시지: " + msg);
                    x.oos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void createRoom(int gameRoomIndex) {
            System.out.println("createRoom method입니다.");
            System.out.println("방번호는 " + gameRoomIndex);
            room = new RoomVO();
            room.setRoomNo(gameRoomIndex);
            gameRoomList.add(room);
            gameRoomIndex++;
        }
    }// Inner class end
}