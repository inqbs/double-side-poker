package gameProject;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import vo.ClientVO;
import vo.RoomVO;

public class Room extends JFrame implements ActionListener, MouseListener{

	BGPanel bg;

	//	body
	JPanel tablePanel, betTablePanel, player1Panel, player2Panel;
	JLabel p1NameLabel, p2NameLabel, p1PointLabel, p2PointLabel, betLabel;
	JButton p1CardBtn, p2CardBtn;

	//	채팅 표시 관련
	JLabel serverMsgLabel;
	BGPanel p1MsgPanel, p2MsgPanel;
	JTextField p1MsgTf, p2MsgTf;

	//	BetTablePanel
	JPanel p1FrontPanel, p1BackPanel, p2FrontPanel, p2BackPanel, betAreaPanel;
	//	p1, p2의 F/B 배팅 패널, 배팅한 칩 보관 패널
	JLabel betCountLabel; // betArea에 모인 칩 갯수 표시

	//	footerPanel
	JPanel footerPanel, betBtnPanel;
	JButton exitBtn, chatBtn;
	JSpinner betValSpn;	// .getValue()로 값 return
	JButton[] actionBtn;
	//	0: front 배팅 or 추가배팅
	//	1: back 배팅 or 콜
	//	2: 양면 배팅 or die
	//	3: 확인버튼

	//	chatBtnPanel
	JPanel chatBtnPanel;
	JLabel msgPreviewLabel;
	JButton[] chatMsgBtn;

	//	로직 동작용
	boolean clickable; // 자신의 카드만 뒷장확인, 게임시작전 클릭 불가능
	boolean frontBetted, backBetted;	//	배팅 장소 판단용
	ImageIcon chipImg;
	JLabel[] chips;
	
	ImageIcon[] cardImgF, cardImgB;
	ImageIcon isReady, notReady;

	int bettedChipsInFront, bettedChipsInBack;

	//	테스트용
	String[] chatMent = new String[8];

	int roomNo;
	RoomVO room;
	ClientVO vo;
	int chatIndex;
	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Thread th;


	Room(){
		setLayout(null);

		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension screenSize = tool.getScreenSize();

		chipImg = new ImageIcon("src/img/chip.png");
		//	칩 이미지

		// 배경
		bg = new BGPanel("src/img/bg_gameRoom.png");
		bg.setBounds(0, 0, Style.WIDTH, Style.HEIGHT);

		//	테이블+footer 기본 설정
		tablePanel = new JPanel();
		footerPanel = new JPanel();

		tablePanel.setBounds(0, 0, 1280, 720-180);
		tablePanel.setLayout(null);
		tablePanel.setOpaque(false);

		footerPanel.setBounds(0, 720-180, 1260, 160);
		footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
		footerPanel.setOpaque(false);
		footerPanel.setBorder(BorderFactory.createEmptyBorder(20,20, 20,20));
		//	안보이는 선을 설정하여 margin 생성

		Dimension d = new Dimension(80, 80);	//	버튼 크기 지정

		//	body(tablePanel)
		betTablePanel = new JPanel();
		betTablePanel.setBounds((Style.WIDTH-330)/2-5,153,330,360);
		betTablePanel.setBackground(Color.GRAY);

		player1Panel = new JPanel();
		player2Panel = new JPanel();
		p1CardBtn = new JButton(new ImageIcon("src/img/card/card_ready.png"));
		p2CardBtn = new JButton(new ImageIcon("src/img/card/card_ready.png"));

		player1Panel.setOpaque(false);
		player1Panel.setLayout(new BoxLayout(player1Panel, BoxLayout.Y_AXIS));
		player2Panel.setOpaque(false);
		player2Panel.setLayout(new BoxLayout(player2Panel, BoxLayout.Y_AXIS));

		p1CardBtn.setOpaque(false);
		p2CardBtn.setOpaque(false);
		p1CardBtn.setBounds(38,160,180,240);
		p2CardBtn.setBounds(1057,160, 180,240);
		p1CardBtn.setContentAreaFilled(false);
		p1CardBtn.setBorderPainted(false);
		p1CardBtn.setFocusPainted(false);
		p2CardBtn.setContentAreaFilled(false);
		p2CardBtn.setBorderPainted(false);
		p2CardBtn.setFocusPainted(false);

		player1Panel.setBounds(28,betTablePanel.getY()+265,200,300);
		player2Panel.setBounds(Style.WIDTH-232,betTablePanel.getY()+265,200,300);


		//	msg 표시부
		p1MsgPanel = new BGPanel("src/img/bg_game_chat.png");
		p2MsgPanel = new BGPanel("src/img/bg_game_chat.png", true);
		p1MsgTf = new JTextField(2);
		p2MsgTf = new JTextField(2);

		//	패널
		p1MsgPanel.setBounds(255, 280, 200, 100);
		p2MsgPanel.setBounds(815, 280, 200, 100);

		p1MsgPanel.setVisible(false);
		p2MsgPanel.setVisible(false);

		p1MsgPanel.setLayout(new BorderLayout());
		p2MsgPanel.setLayout(new BorderLayout());

		//	TF
		p1MsgTf.setFont(Style.subFont);
		p2MsgTf.setFont(Style.subFont);

		p1MsgTf.setOpaque(false);
		p2MsgTf.setOpaque(false);

		p1MsgTf.setEditable(false);
		p2MsgTf.setEditable(false);

		p1MsgTf.setBorder(null);
		p2MsgTf.setBorder(null);

		p1MsgTf.setHorizontalAlignment(SwingConstants.CENTER);
		p2MsgTf.setHorizontalAlignment(SwingConstants.CENTER);

		p1MsgPanel.add(new JLabel("  "), "West");
		p2MsgPanel.add(new JLabel("\t") , "East");

		p1MsgPanel.add(p1MsgTf, "Center");
		p2MsgPanel.add(p2MsgTf, "Center");

		add(p1MsgPanel);
		add(p2MsgPanel);


		//	betTablePanel
		betTablePanel.setOpaque(false);
		betTablePanel.setLayout(null);

		p1FrontPanel = new JPanel();
		p1BackPanel = new JPanel();
		betAreaPanel = new JPanel();
		betCountLabel = new JLabel("0 개", SwingConstants.CENTER);
		p2FrontPanel = new JPanel();
		p2BackPanel = new JPanel();

		betCountLabel.setFont(Style.mainFont);
		betCountLabel.setForeground(Color.WHITE);

		p1FrontPanel.setOpaque(false);
		p1BackPanel.setOpaque(false);
		p2FrontPanel.setOpaque(false);
		p2BackPanel.setOpaque(false);
		betAreaPanel.setOpaque(false);

//		p1FrontPanel.setBackground(Color.CYAN);
//		p1BackPanel.setBackground(Color.PINK);
//		p2FrontPanel.setBackground(Color.CYAN);
//		p2BackPanel.setBackground(Color.PINK);

		FlowLayout fl = new FlowLayout(FlowLayout.CENTER, -5, -5);

		p1FrontPanel.setLayout(fl);
		p1FrontPanel.setBorder(Style.PADDING);
		p1BackPanel.setLayout(fl);
		p1BackPanel.setBorder(Style.PADDING);
		betAreaPanel.setLayout(fl);
		betAreaPanel.setBorder(Style.PADDING);
		p2FrontPanel.setLayout(fl);
		p2FrontPanel.setBorder(Style.PADDING);
		p2BackPanel.setLayout(fl);
		p2BackPanel.setBorder(Style.PADDING);

		p1FrontPanel.setBounds(0, 0, 110, 180);
		p1BackPanel.setBounds(0, 180, 110, 180);
		betAreaPanel.setBounds(110, 0, 115, 300);
		betCountLabel.setBounds(110+15,310, 80, 24);
		p2FrontPanel.setBounds(220, 0, 110, 180);
		p2BackPanel.setBounds(220, 180, 110, 180);

		betTablePanel.add(p1FrontPanel);
		betTablePanel.add(p1BackPanel);
		betTablePanel.add(betAreaPanel);
		betTablePanel.add(betCountLabel);
		betTablePanel.add(p2FrontPanel);
		betTablePanel.add(p2BackPanel);

		//	GUI 테스트용 (칩이 제대로 add 되는지)
//	    int n = 3;
//	    for(int i = 0; i<n; i++)
//	    	p1FrontPanel.add(new JLabel(chipImg));

//	    n = 6;
//	    for(int i = 0; i<n; i++)
//	    	p1BackPanel.add(new JLabel(chipImg));
//
//	    n = 50;
//	    for(int i = 0; i<n; i++)
//	    	betAreaPanel.add(new JLabel(chipImg));
//
//	    n = 8;
//	    for(int i = 0; i<n; i++)
//	    	p2FrontPanel.add(new JLabel(chipImg));
//
//	    n = 9;
//	    for(int i = 0; i<n; i++)
//	    	p2BackPanel.add(new JLabel(chipImg));


		//	p1, p2
		p1NameLabel = new JLabel("Player 1", SwingConstants.CENTER);
		p2NameLabel = new JLabel("Player 2", SwingConstants.CENTER);
		p1PointLabel = new JLabel("포인트", SwingConstants.CENTER);
		p2PointLabel = new JLabel("포인트", SwingConstants.CENTER);

		p1NameLabel.setFont(Style.mainFont);
		p2NameLabel.setFont(Style.mainFont);
		p1PointLabel.setFont(Style.mainFont);
		p2PointLabel.setFont(Style.mainFont);

		p1NameLabel.setMaximumSize(new Dimension(200, 18));
		p2NameLabel.setMaximumSize(new Dimension(200, 18));
		p1PointLabel.setMaximumSize(new Dimension(200, 56));
		p2PointLabel.setMaximumSize(new Dimension(200, 56));

		p2NameLabel.setForeground(Color.WHITE);
		p2PointLabel.setForeground(Color.WHITE);

		player1Panel.add(p1NameLabel);
		player1Panel.add(Box.createRigidArea(new Dimension(1,18)));
		player1Panel.add(p1PointLabel);
		player2Panel.add(p2NameLabel);
		player2Panel.add(Box.createRigidArea(new Dimension(1,18)));
		player2Panel.add(p2PointLabel);


		//	table패널에 부착
		tablePanel.add(betTablePanel);
		tablePanel.add(player1Panel);
		tablePanel.add(player2Panel);
		tablePanel.add(p1CardBtn);
		tablePanel.add(p2CardBtn);


		//	footerPanel
		exitBtn = new JButton(new ImageIcon("src/img/btn_room_exit.png"));
		betBtnPanel = new JPanel();
		chatBtn = new JButton(new ImageIcon("src/img/btn_room_chat.png"));

		exitBtn.setOpaque(false);
		exitBtn.setContentAreaFilled(false);
		exitBtn.setBorderPainted(false);
		exitBtn.setFocusPainted(false);
		chatBtn.setOpaque(false);
		chatBtn.setContentAreaFilled(false);
		chatBtn.setBorderPainted(false);
		chatBtn.setFocusPainted(false);


		betBtnPanel.setLayout(new GridLayout(1, 3, 20, 0));
		betBtnPanel.setOpaque(false);

		betValSpn = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
		//	SpinnerNumberModel(초기값, 최소값, 최대값, 증가값);

		betBtnPanel.add(betValSpn);

		exitBtn.setMaximumSize(d);
		betBtnPanel.setMinimumSize(new Dimension(340, 50));
		betBtnPanel.setMaximumSize(new Dimension(340, 50));
		chatBtn.setMaximumSize(d);

		actionBtn = new JButton[4];
//		int j = 0;
//		for(JButton x : actionBtn) {
		for(int i=0; i<actionBtn.length; i++) {
			actionBtn[i] = new JButton(new ImageIcon("src/img/btn_game_first_"+(i)+".png"));
			actionBtn[i].setMaximumSize(d);
			actionBtn[i].setContentAreaFilled(false);
			actionBtn[i].setBorderPainted(false);
			actionBtn[i].setFocusPainted(false);
			actionBtn[i].addActionListener(this);
			betBtnPanel.add(actionBtn[i]);
		}

		System.out.println(actionBtn[0]);

		//	chatBtnPanel
		msgPreviewLabel = new JLabel("", SwingConstants.CENTER);
		msgPreviewLabel.setBounds(900, 450, 240, 36);
		msgPreviewLabel.setFont(Style.subFont);
		msgPreviewLabel.setBackground(new Color(1.0f,1.0f,1.0f, 0.7f));
		msgPreviewLabel.setOpaque(true);
		msgPreviewLabel.setVisible(false);
		add(msgPreviewLabel);

		chatBtnPanel = new JPanel();
		chatBtnPanel.setLayout(new GridLayout(2, 3, 4, 4));
		chatBtnPanel.setBounds(900-8,500-4, 240+8, 160+4);
		chatBtnPanel.setOpaque(false);

		chatMsgBtn = new JButton[6];
//		i=0;
//		for(JButton x : chatMsgBtn) {
		for(int i=0; i<chatMsgBtn.length; i++) {
			chatMsgBtn[i] = new JButton(new ImageIcon("src/img/emoji/btn_chat_"+(i+1)+".png"));
			chatMsgBtn[i].setContentAreaFilled(false);
			chatMsgBtn[i].setBorderPainted(false);
			chatMsgBtn[i].setFocusPainted(false);
			chatMsgBtn[i].addActionListener(this);
			chatMsgBtn[i].addMouseListener(this);
			chatBtnPanel.add(chatMsgBtn[i]);
		}

		chatBtnPanel.setVisible(false);
		add(chatBtnPanel);

		footerPanel.add(exitBtn);
		footerPanel.add(Box.createRigidArea(new Dimension((Style.WIDTH-700)/2,50)));
		footerPanel.add(betBtnPanel);
		footerPanel.add(Box.createRigidArea(new Dimension((Style.WIDTH-700)/2,50)));
		footerPanel.add(chatBtn);

		p1CardBtn.addActionListener(this);
		p2CardBtn.addActionListener(this);
		exitBtn.addActionListener(this);
		chatBtn.addActionListener(this);
		
		//	서버로 부터 메시지
		serverMsgLabel = new JLabel("여기에 메시지가 들어감", SwingConstants.CENTER);
		serverMsgLabel.setOpaque(true);
		serverMsgLabel.setBounds((Style.WIDTH-400)/2, 50, 400, 50);
		serverMsgLabel.setBackground(Color.DARK_GRAY);
		serverMsgLabel.setForeground(Color.WHITE);
		serverMsgLabel.setFont(Style.mainFont);
		add(serverMsgLabel);
		
		
		add(tablePanel);
		add(footerPanel);
		add(bg);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setBounds((int)(screenSize.getWidth()-Style.WIDTH)/2, (int)(screenSize.getHeight()-Style.HEIGHT)/2, Style.WIDTH, Style.HEIGHT);
		setVisible(true);
		
		cardImgF = new ImageIcon[10];
		cardImgB = new ImageIcon[10];
		
		for(int i = 0; i < cardImgF.length; i++) {
			cardImgF[i] = new ImageIcon("src/img/card/"+(i+1)+"f.png");
			cardImgB[i] = new ImageIcon("src/img/card/"+(i+1)+"b.png");
		}
		

	}

	Room(ClientVO vo, int roomNo, ObjectInputStream ois, ObjectOutputStream oos){
		this();
//		this.s = s;
		this.vo = vo;
		this.roomNo = roomNo;
		this.ois = ois;
		this.oos = oos;
//		this.vo.setReadyState(false);
		chatIndex  = -1;
		isReady = new ImageIcon("src/img/card/card_ready_readied.png");
		notReady = new ImageIcon("src/img/card/card_ready.png");
		

		// 아래 두개 중에 무었을 먼저 실행해야 될까?
		startThread();
		closeBtn();
		System.out.println("Room 생성 성공");
	}
	
	private void startThread(){ // thread 관련 처리는 여기서, inner class로 한번 해봄
		
		//	최초 실행시 room 정보 갱신 요청
		try {
			oos.writeObject("/ROORE "+roomNo);
			oos.reset();
			System.out.println(vo.getUserCode()+"가 room_refresh 실행");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		th = new Thread(new Runnable() {
			@Override
			public void run() {
				//Lobby에서 넘겨 받은 애들이 정상적으로 잘 있는지 확인
				System.out.println("Room.java/run method ==========================================================");
				
				while(true) {
					try {
						Object obj = ois.readObject();
						
						if(obj instanceof RoomVO) {
							room = (RoomVO)obj;
							
							System.out.println("게임 진행중 : "+ room.isplaying());
							
							System.out.println("p1: " + room.getP1UserName());
							System.out.println("ready: " + room.isP1Ready());
							System.out.println("p2: " + room.getP2UserName());
							System.out.println("ready: " + room.isP2Ready());
							
							//	룸 정보 서버와 동기화
							p1NameLabel.setText(room.getP1UserName());
							p2NameLabel.setText(room.getP2UserName());
							if(room.getP1UserName().equals(vo.getNickName()))
								p1NameLabel.setText("나 : "+room.getP1UserName());
							else if(room.getP2UserName().equals(vo.getNickName()))
								p2NameLabel.setText("나 : "+room.getP2UserName());
								
							p1PointLabel.setText(""+room.getP1Chips());
							p2PointLabel.setText(""+room.getP2Chips());
							
							p1FrontPanel.removeAll();
							p1BackPanel.removeAll();
							
							switch(room.getP1FrontBack()) {
							case 0:	//	앞면
								for(int i=0; i<room.getP1BetChips();i++)
									p1FrontPanel.add(new JLabel(chipImg));
								break;
							case 1:	//	뒷면
								for(int i=0; i<room.getP1BetChips();i++)
									p1BackPanel.add(new JLabel(chipImg));
								break;
							case 2:	//	양면
								for(int i=0; i<room.getP1BetChips();i++) {
									p1FrontPanel.add(new JLabel(chipImg));
									p1BackPanel.add(new JLabel(chipImg));
								}
								break;
							}
							
							p2FrontPanel.removeAll();
							p2BackPanel.removeAll();
							
							switch(room.getP2FrontBack()) {
							case 0:	//	앞면
								for(int i=0; i<room.getP1BetChips();i++)
									p2FrontPanel.add(new JLabel(chipImg));
								break;
							case 1:	//	뒷면
								for(int i=0; i<room.getP1BetChips();i++)
									p2BackPanel.add(new JLabel(chipImg));
								break;
							case 2:	//	양면
								for(int i=0; i<room.getP1BetChips();i++) {
									p2FrontPanel.add(new JLabel(chipImg));
									p2BackPanel.add(new JLabel(chipImg));
								}
								break;
							}
							
							betAreaPanel.removeAll();
							for(int i=0; i<room.getCenterBetChips(); i++)
								betAreaPanel.add(new JLabel(chipImg));
							
							betPanelRefresh();
							
							
							//	게임룸 정보 동기화 완료
							//	이후 로직 수행
							
							if(room.isplaying()) {
								//	게임 진행중일때
//								serverMsgLabel.setVisible(false);
								
							} else {
								//	게임 진행중이 아닐떄
								
								//	플레이어 레디 판정에 따라 버튼 repaint
								if(room.isP1Ready()) {
									System.out.println("p1이 ready");
									p1CardBtn.setIcon(isReady);
								}else if(room.isP2Ready()) {
									System.out.println("p2가 ready");
									p2CardBtn.setIcon(isReady);
								}else if(!room.isP1Ready()&&!room.isP2Ready()) {
									System.out.println("p1 p2가 ready가 아님");
									p1CardBtn.setIcon(notReady);
									p2CardBtn.setIcon(notReady);
								}
								
								
							}
							
							
						}else if(obj instanceof String) {
							String readString = obj.toString().trim();
							System.out.println("CLient가 요청한 메시지" + readString);
							String order = readString.substring(1, 6);	//	명령어
		                    String value = readString.substring(7);	//	값	
		                    
		                    switch(order) {
		                    case "RMCOM":
		                    	String valueList[] = value.split(",");
		                    	String tmpUserCode = valueList[0];
		                    	int commentIndex = Integer.parseInt(valueList[1]);
		                    	
		                    	System.out.println("말한 사람의 usercode" + tmpUserCode);
		                    	
		                    	if(tmpUserCode.equals(room.getP1UserCode())) {
		                    		//	1p의 대화창에 commentIndex에 따른 말 표시
		                    		p1MsgPanel.setVisible(true);
		            				//	채팅 내용 표시
		            				String msg = chatMent[commentIndex];
		            				p1MsgTf.setText(msg);
		            				//	일정 시간후 채팅내용 x 라벨 비표시
		            				TimerTask task = new TimerTask() {
		            					@Override
		            					public void run() {
		            						//	5초후의 메시지가 5초전의 메시지와 같다
		            						//	== 5초 동안 아무런 메시지도 보내지 않았다.
		            						if(p1MsgTf.getText().equals(msg)) {
		            							p1MsgTf.setText("");
		            							p1MsgPanel.setVisible(false);
		            						}
		            					}
		            				};
		            				new Timer().schedule(task, 5000);
		                    	}else if(tmpUserCode.equals(room.getP2UserCode())) {
		                    		//	2p의 대화창에 위와동일
		                    		p2MsgPanel.setVisible(true);
		            				//	채팅 내용 표시
		            				String msg = chatMent[commentIndex];
		            				p2MsgTf.setText(msg);
		            				//	일정 시간후 채팅내용 x 라벨 비표시
		            				TimerTask task = new TimerTask() {
		            					@Override
		            					public void run() {
		            						//	5초후의 메시지가 5초전의 메시지와 같다
		            						//	== 5초 동안 아무런 메시지도 보내지 않았다.
		            						if(p2MsgTf.getText().equals(msg)) {
		            							p2MsgTf.setText("");
		            							p2MsgPanel.setVisible(false);
		            						}
		            					}
		            				};
		            				new Timer().schedule(task, 5000);
		                    	}
		                    	break;
		                    case "ALERT":
		                    	serverMsgLabel.setText(value);
		                    	serverMsgLabel.setVisible(true);
		                    	break;
		                    }
							
						}
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					
				}//	end while
				
			}// end run method
		});
		th.start();
//		chatIndex = -1;
		
		// thread 쓰고나서 초기화
		
	} // end startThreaed method

	private void sendRoom(GameRoom gr) {
		try {
			oos.writeObject(gr);
			oos.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendVO(ClientVO vo) {
		try {

			oos.writeObject(vo);
			oos.reset();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private void updateRoom() {
//		// update는 gameGui에 모든 패널, 버튼, 등등 다 업데이트 하는데 로직에 따라 발생하는 NULL예외처리를 잘 해야된다.
//		System.out.println("updateRoom method 들어오나?");
//			System.out.println("업데이트를 합니다.");
//		
//			//Integer culbet = gr.getCumulativeBattingPoint();
//			
//			System.out.println("client[0] : "+ gr.getClient()[0]
//					+", client[1] : " + gr.getClient()[1]);
//			
//			if(gr.getClient()[0] == null && gr.getClient()[1] != null) {
//				Integer cl2Point = gr.getClient()[1].getPoint();
//				p2NameLabel.setText(gr.getClient()[1].getNickName());
//				p2PointLabel.setText(cl2Point.toString());
//				//betLabel.setText(culbet.toString());
//			}else if(gr.getClient()[0] != null && gr.getClient()[1] == null) {
//				Integer cl1Point = gr.getClient()[0].getPoint();
//				p1NameLabel.setText(gr.getClient()[0].getNickName());
//				p1PointLabel.setText(cl1Point.toString());
//				//betLabel.setText(culbet.toString());
//			}else if(gr.getClient()[0] != null && gr.getClient()[1] != null) {
//				Integer cl2Point = gr.getClient()[1].getPoint();
//				p2NameLabel.setText(gr.getClient()[1].getNickName());
//				p2PointLabel.setText(cl2Point.toString());
//				Integer cl1Point = gr.getClient()[0].getPoint();
//				p1NameLabel.setText(gr.getClient()[0].getNickName());
//				p1PointLabel.setText(cl1Point.toString());
//				//betLabel.setText(culbet.toString());
//			}
//			
//		}// end update method
	
//	private void showCard(DoubleSideCard card) {
//		// setImgage를 카드로 바꾼다. 자신의 앞면 상대방의 앞면을 볼 수 있도록 둔다.
//		if(gr.returnOwnData(vo) == 0) {			
//			p1CardBtn.setIcon(new ImageIcon("src/img/card"+card.front+"f.png"));
//		}else if(gr.returnOwnData(vo) == 1) {
//			p2CardBtn.setIcon(new ImageIcon("src/img/card"+card.front+"f.png"));			
//		}
//	}

	private void closeBtn() {
		for(int i = 0 ; i < 4; i++) {
			actionBtn[i].setEnabled(false);
		}
	}
	
	private void openBtn() {
		for(int i = 0 ; i < 4; i++) {
			actionBtn[i].setEnabled(true);
		}
	}
//	private void chaingeCardImage(DoubleSideCard card) {
//		System.out.println("카드의 앞 뒷면을 바꿉니다.");
//		
//		if(gr.returnOwnData(vo) == 0) {			
//			p1CardBtn.setIcon(new ImageIcon("src/img/card"+card.back+"b.png"));
//		}else if(gr.returnOwnData(vo) == 1) {
//			p2CardBtn.setIcon(new ImageIcon("src/img/card"+card.back+"b.png"));			
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exitBtn) {
			//	종료버튼 누르면 로비로 가는 로직
			this.dispose();	//	임시용 닫기 코드
		}else if(e.getSource() == p1CardBtn) { // 여기서 1p ready 버튼
			//	p1 card 버튼
			
			if(room.getP1UserCode().equals(""+vo.getUserCode())) {
				try {
					oos.writeObject("/READY "+vo.getUserCode());
					oos.reset();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//			}else if(room.isplaying()) {
//				//	게임 진행 중일때...
			}

		}else if(e.getSource() == p2CardBtn) { // 여기서 2p ready 버튼
			//	p2 card 버튼
			if(room.getP2UserCode()!=null && room.getP2UserCode().equals(""+vo.getUserCode())) {
				try {
					oos.writeObject("/READY "+vo.getUserCode());
					oos.reset();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//			}else if(room.isplaying()) {
//				//	게임 진행 중일때...
			}

		}else if(e.getSource() == chatBtn) {
			//	채팅 버튼
			if(chatBtnPanel.isVisible()) {
				msgPreviewLabel.setVisible(false);
				chatBtnPanel.setVisible(false);
				chatBtn.setIcon(new ImageIcon("src/img/btn_room_chat.png"));
			}else {
				msgPreviewLabel.setVisible(true);
				chatBtnPanel.setVisible(true);
				chatBtn.setIcon(new ImageIcon("src/img/btn_room_chat_close.png"));
			}
		}
		//	0: front 배팅 or 추가배팅
		//	1: back 배팅 or 콜
		//	2: 양면 배팅 or die
		//	3: 확인버튼
		else if(e.getSource() == actionBtn[0]) {
			
			if(actionBtn[0].getIcon().toString().contains("first")) {
				//	front 배팅 일때
				if(bettedChipsInBack>0 && bettedChipsInFront>0) {
					//	양면배팅을 한 후라면
					p1FrontPanel.removeAll();
					p1BackPanel.removeAll();
					bettedChipsInFront = 0;
					bettedChipsInBack = 0;
				}else {
					p1BackPanel.removeAll();
					bettedChipsInBack = 0;
				}
				bettedChipsInFront += (int)betValSpn.getValue();
				
				for(int i=0; i < (int)betValSpn.getValue(); i++)
					p1FrontPanel.add(new JLabel(chipImg));
				
				betPanelRefresh();
			}else {
				//	추가배팅일때
				
				//	a:상대방의 건 칩개수와 b:betValSpn의 값을 getValue해서
				
				//	배팅
				
//				betSwitch(a, b);			
				betSwitch(5, (int)betValSpn.getValue());			
			}
			

		}else if(e.getSource() == actionBtn[1]) {
			if(actionBtn[1].getIcon().toString().contains("first")) {
				//	back 배팅 일때
				if(bettedChipsInBack>0 && bettedChipsInFront>0) {
					//	양면배팅을 한 후라면
					p1FrontPanel.removeAll();
					p1BackPanel.removeAll();
					bettedChipsInFront = 0;
					bettedChipsInBack = 0;
				}else {
					p1FrontPanel.removeAll();
					bettedChipsInFront = 0;
				}
				bettedChipsInBack += (int)betValSpn.getValue();
	
				for(int i=0; i < (int)betValSpn.getValue(); i++)
					p1BackPanel.add(new JLabel(chipImg));
	
				betPanelRefresh();
			}else {
				//	콜일때
				
				//	a:상대방의 건 칩개수를 get해서 배팅
//				betSwitch(a, 0);
				betSwitch(10, 0);
				
			}

		}else if(e.getSource() == actionBtn[2]) {
			if(actionBtn[1].getIcon().toString().contains("first")) {
				//	양면 배팅일때
				if(bettedChipsInBack==0||bettedChipsInFront==0) {
					p1FrontPanel.removeAll();
					p1BackPanel.removeAll();
					bettedChipsInFront = 0;
					bettedChipsInBack = 0;
				}
	
				bettedChipsInFront += (int)betValSpn.getValue();
				bettedChipsInBack += (int)betValSpn.getValue();
	
				for(int i=0; i < (int)betValSpn.getValue(); i++) {
					p1FrontPanel.add(new JLabel(chipImg));
					p1BackPanel.add(new JLabel(chipImg));
				}
	
				betPanelRefresh();
			}else {
				//	다이일때
				
				//	게임종료로직(자신의 패로)
			}

		}else if(e.getSource() == actionBtn[3]) {
			//	서버로 보내는 데이터 관련 로직은 모두 여기에 들어가야 한다.
			//!!!!================================================================================!!!
			if(bettedChipsInBack>0||bettedChipsInFront>0) {
				int result = JOptionPane.showConfirmDialog(null, "이대로 배팅하시겠습니까?", "배팅확인",JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("배팅 ㄱ");
					p1FrontPanel.removeAll();
					p1BackPanel.removeAll();

//					if(bettedChipsInBack>0&&bettedChipsInFront>0) {
//						//	양면 배팅 판별
//						gr.setCumulativeBattingPoint(bettedChipsInBack+bettedChipsInFront);
//					}else if(bettedChipsInFront>0) {
//						//	앞면 배팅 판별
//						frontBetted = true;
//						gr.setCumulativeBattingPoint(bettedChipsInFront);
//					}else {
//						//	뒷면 배팅 판별
//						backBetted = true;
//						gr.setCumulativeBattingPoint(bettedChipsInBack);
//						
//					}

//					int bettedChips = bettedChipsInFront + bettedChipsInBack;

//					for(int i=0; i<bettedChips; i++)
//						betAreaPanel.add(new JLabel(chipImg));
//
//					betCountLabel.setText(bettedChips+ " 개");

					betPanelRefresh();

					//	배팅정보 송신부 => 보내고 쓰레드를 실행시켜서 대기를 한다.

					//	배팅 후 역할 교체
					//	0:front -> 추가배팅
					//	1:back -> 콜
					//	2:양면 -> die
					//	이미지 교체 코드
					for(int i=0; i<actionBtn.length; i++)
						actionBtn[i].setIcon(new ImageIcon("src/img/btn_game_sec_"+(i)+".png"));


					//	배팅 후 초기화
//					bettedChipsInFront = 0;
//					bettedChipsInBack = 0;
					

					betValSpn.setValue(1);
				}
			}
			else JOptionPane.showMessageDialog(null, "배팅을 하지 않으셨습니다.");

		}

	}

	private void betPanelRefresh() {
		p1FrontPanel.revalidate();
		p1BackPanel.revalidate();
		p2FrontPanel.revalidate();
		p2BackPanel.revalidate();
		betAreaPanel.revalidate();
		
		p1FrontPanel.repaint();
		p1BackPanel.repaint();
		p2FrontPanel.repaint();
		p2BackPanel.repaint();
		betAreaPanel.repaint();
	}

	
	private void betSwitch(int a, int b) {
		//	a:상대방의 건 칩개수, b:betValSpn의 값
		int tmp = a+b; 
		if(bettedChipsInBack>0 && bettedChipsInFront>0) {
			//	양면배팅이라면 양면배팅식
			bettedChipsInBack += tmp;
			bettedChipsInFront += tmp;
			
			for(int i=0; i < tmp; i++) {
				p1FrontPanel.add(new JLabel(chipImg));
				p1BackPanel.add(new JLabel(chipImg));
			}
			
		}else if(bettedChipsInFront>0) {
			//	이전에 앞면에 걸었다면 앞면에 추가
			bettedChipsInFront += tmp;
			
			for(int i=0; i < tmp; i++) 
				p1FrontPanel.add(new JLabel(chipImg));
			
		}else {
			//	이전에 뒷면에 걸었다면 뒷면에 추가
			bettedChipsInBack += tmp;
			
			for(int i=0; i < tmp; i++) 
				p1BackPanel.add(new JLabel(chipImg));
		}
		betPanelRefresh();
	}
	// ================ 밑에는 내가 손 안되도 됨 ================== 좀있다 댐
	private void requestRefreshInfo(ClientVO vo) { // 처음 한번만 실행되는 메서드
		System.out.println("refesh 실행합니다.");
		try {
			// 오브젝트 보냄
//			vo.setOrderMsg("reqGameRoom");
			oos.writeObject(vo);
			oos.reset();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//	GameRoom의 배팅된 chip정보를 표시하기 위한 메소드
	private void setChipImg(int in1f, int in1b, int c, int in2f, int in2b) {
		chips = new JLabel[in1f];
		int i=0;
		for(JLabel x : chips) {
			x = new JLabel(chipImg);
			x.setLocation(0, i);
			p1FrontPanel.add(x);
			i+=10;
		}

		chips = new JLabel[in1b];
		i=0;
		for(JLabel x : chips) {
			x = new JLabel(chipImg);
			x.setLocation(0, i);
			p1BackPanel.add(x);
			i+=10;
		}

		chips = new JLabel[c];
		i=0;
		for(JLabel x : chips) {
			x = new JLabel(chipImg);
			x.setLocation(0, i);
			betAreaPanel.add(x);
			i+=10;
		}

		chips = new JLabel[in2f];
		i=0;
		for(JLabel x : chips) {
			x = new JLabel(chipImg);
			x.setLocation(0, i);
			p2FrontPanel.add(x);
			i+=10;
		}

		chips = new JLabel[in2b];
		i=0;
		for(JLabel x : chips) {
			x = new JLabel(chipImg);
			x.setLocation(0, i);
			p2BackPanel.add(x);
			i+=10;
		}
	}


	//	배경이미지를 패널에 출력하기 위한 class BGPanel
	class BGPanel extends JPanel{
		Image img; boolean reverse;

		public BGPanel(String src){
			img = Toolkit.getDefaultToolkit().createImage(src);
			setOpaque(false);
		}
		public BGPanel(String src, Boolean reverse) {
			this(src);
			this.reverse = reverse;
		}
		@Override
		public void paint(Graphics g) {
			if(!reverse) g.drawImage(img, 0, 0, img.getWidth(this), img.getHeight(this), this);
			else g.drawImage(img, img.getWidth(this), 0, img.getWidth(this) * -1, img.getHeight(this), this);
			super.paint(g);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i=0; i<6 ;i++) {
			if(e.getSource() == chatMsgBtn[i]) {
				//	채팅 서버로 송신
				try {
					oos.writeObject("/RMCOM "+roomNo+","+vo.getUserCode()+","+i);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		for(int i=0; i<6 ;i++) {
			if(e.getSource() == chatMsgBtn[i]) {
				chatInit();
				msgPreviewLabel.setText(chatMent[i]);
				msgPreviewLabel.setOpaque(true);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		for(int i=0; i<6; i++) {
			if(e.getSource() == chatMsgBtn[i]) {
				msgPreviewLabel.setText("");
				msgPreviewLabel.setOpaque(false);
			}
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

}//class end