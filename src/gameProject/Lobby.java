package gameProject;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import vo.ClientVO;

public class Lobby extends JFrame implements ActionListener, WindowListener, KeyListener, Runnable{

	ObjectOutputStream oos;
	ObjectInputStream ois;
	Roulette rl;

	Toolkit tool = Toolkit.getDefaultToolkit();

	JPanel	header, body, footer;
	JPanel 	namePanel, infoPanel,	//	header
			menuPanel,	//	body
			chatPanel;	//	footer

	JButton roomBtn, helpBtn, rouletteBtn, //	body
			shutdownBtn;	//	footer

	JLabel bg, nameLabel, lvLabel, expLabel, expGraphLabel, pointLabel;	//	header

	ImageIcon img;


	JScrollPane chatBoard;
	JTextField chatField;
	JTextArea chatArea;
	JPanel jp;

	ClientVO vo;
	Socket s;

	int userCode, exp, lv, point, count;
	String id, name;

	Thread tr;
	boolean enterRoomState;

	Lobby(){
		Dimension d = tool.getScreenSize();	//	嶸�椪 �怦�巡�垈 﨑ｴ�メ�巡增ｬ�ｸｰ �攷�牟�亢
		//	Style.WIDTH = 1280;
		//	Style.HEIGHT = 720;

		//	window �ｸｰ�ｳｸ �└���
		setLayout(null);
		setBounds((int)((d.getWidth()-Style.WIDTH)/2)-1,(int)((d.getHeight()-Style.HEIGHT)/2)-1, Style.WIDTH, Style.HEIGHT);
		setTitle("Lobby :: ONLINE DOUBLE POKER");
		//	�怦�巡�垈 ��菩､卓蕗�乱 1280*720 �ぎ�擽�ｦ溢攪 �ｰｽ
		this.setResizable(false);	//	�ｰｽ�｡ｰ��� �ｶ一ｰ��冠﨑俾ｲ�

		//	�ｰｰ�ｲｽ
		bg = new JLabel();
		ImageIcon bgIcon = new ImageIcon();
		try {
			bgIcon.setImage(ImageIO.read(new File("src/img/bg.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		bg.setIcon(bgIcon);

		bg.setBounds(0, 0, Style.WIDTH, Style.HEIGHT);

		header = new JPanel();
		body = new JPanel();
		footer = new JPanel();

		header.setLayout(null);
		header.setOpaque(false);
		header.setBounds(0, 0, Style.WIDTH, 80);
		body.setLayout(null);
		body.setOpaque(false);
		body.setBounds(0, 80, Style.WIDTH, Style.HEIGHT-80);
		footer.setLayout(null);
		footer.setOpaque(false);
		footer.setBounds(0, Style.HEIGHT-160, Style.WIDTH, 200);

		//	header
		namePanel = new BGPanel("src/img/label_name.png");
		infoPanel = new BGPanel("src/img/label_info.png");

		namePanel.setLayout(null);
		namePanel.setBounds(0, 0, 360, 120);
		namePanel.setBackground(Color.GRAY);

		infoPanel.setLayout(null);
		infoPanel.setBounds(Style.WIDTH - 560, 0, 560, 240);
		infoPanel.setBackground(Color.GRAY);


		//	�└�桷甯ｨ�ю
		nameLabel = new JLabel("",SwingConstants.CENTER);
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(Style.infoFont);
		nameLabel.setBounds(100, 22, 100, 30);


		//	�攤尞ｬ甯ｨ�ю
		lvLabel = new JLabel("LV. ");
		expLabel = new JLabel("exp. ");
		expGraphLabel = new JLabel("######");
		pointLabel = new JLabel("0");

		lvLabel.setBounds(160,24, 80,30);
		lvLabel.setFont(Style.infoFont);
		lvLabel.setForeground(Color.WHITE);

		expLabel.setBounds(320,24, 80,30);
		expLabel.setFont(Style.infoFont);
		expLabel.setForeground(Color.WHITE);

		pointLabel.setBounds(320,24, 150,30);
		pointLabel.setFont(Style.infoFont);
		pointLabel.setForeground(Color.WHITE);
		pointLabel.setHorizontalAlignment(SwingConstants.RIGHT);


		namePanel.add(nameLabel);
		infoPanel.add(lvLabel);
		infoPanel.add(expLabel);
		infoPanel.add(expGraphLabel);
		infoPanel.add(pointLabel);

		header.add(namePanel);
		header.add(infoPanel);

		//	body
		menuPanel = new JPanel(new GridLayout(1, 3, 48, 0));
		menuPanel.setOpaque(false);
		menuPanel.setBounds((body.getWidth()-960)/2, 80, 960, 360);

		//	�ｲ�孖ｼ �ぎ�擽�ｦ� : 288, 360
		roomBtn = new JButton(new ImageIcon("src/img/btn_room.png"));
		helpBtn = new JButton(new ImageIcon("src/img/btn_help.png"));
		rouletteBtn = new JButton(new ImageIcon("src/img/btn_rullet.png"));

		roomBtn.setOpaque(false);
		roomBtn.setContentAreaFilled(false);
		roomBtn.setBorderPainted(false);
		roomBtn.setFocusPainted(false);
		helpBtn.setOpaque(false);
		helpBtn.setContentAreaFilled(false);
		helpBtn.setBorderPainted(false);
		helpBtn.setFocusPainted(false);
		rouletteBtn.setOpaque(false);
		rouletteBtn.setContentAreaFilled(false);
		rouletteBtn.setBorderPainted(false);

		menuPanel.add(roomBtn);
		menuPanel.add(helpBtn);
		menuPanel.add(rouletteBtn);
		body.add(menuPanel);


		//	footer
		jp = new JPanel();
		jp.setOpaque(false);


		chatPanel = new BGPanel("src/img/bg_chat.png");
		chatPanel.setBounds(0, 0, 480, 132);
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setOpaque(false);

		chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setFont(Style.subFont);
		chatArea.setOpaque(false);
		chatArea.setMargin(new Insets(10, 10, 10, 10));
		chatArea.setForeground(Color.WHITE);

		chatBoard = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatBoard.setOpaque(false);
		chatBoard.getViewport().setOpaque(false);
		chatBoard.setBorder(null);

		chatField = new JTextField(40);
		chatField.setFont(Style.subFont);
		chatField.setOpaque(false);
		chatField.setBorder(null);

		jp.add(chatField);

		chatPanel.add(chatBoard, "Center");
		chatPanel.add(jp, "South");

		shutdownBtn = new JButton(new ImageIcon("src/img/btn_lobby_exit.png"));
		shutdownBtn.setBounds(Style.WIDTH-140,0,120,120);
		shutdownBtn.setContentAreaFilled(false);
		shutdownBtn.setBorderPainted(false);
		shutdownBtn.setFocusPainted(false);

		footer.add(chatPanel);
		footer.add(shutdownBtn);


		roomBtn.addActionListener(this);
		helpBtn.addActionListener(this);
		rouletteBtn.addActionListener(this);
		shutdownBtn.addActionListener(this);

		chatField.addKeyListener(this);

		addWindowListener(this);

		add(header);
		add(body);
		add(footer);
		add(bg);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	} // gui 관련 생성자

	Lobby(Socket s, ClientVO vo, ObjectOutputStream oos, ObjectInputStream ois){
		this();
		this.vo = vo;
		this.oos = oos;
		this.ois = ois;
		this.s = s; // client에서 사용하던 소켓을 그대로 쓰기 위해서 가져옴
		
		userCode = this.vo.getUserCode();
		name = this.vo.getNickName();
		nameLabel.setText(name);
		
		tr = new Thread(this);
		tr.start();
		
//		System.out.println("로비 생성 성공");
//		System.out.println(" client에서 보내는 VO와 내용이 일치해야 됩니다. 체크포인트 "+this.vo);
//		System.out.println("로비에서 받고 lobby에 저장한 vo : " + this.vo + ", oos: " + this.oos + ", ois: " + this.ois);
//		System.out.println(" client에서 보내는 VO와 내용이 일치해야 됩니다. 체크포인트2 "+vo);
//		System.out.println("로비에서 받는 vo : " + vo + ", oos: " + oos + ", ois: " + ois);
	}

	//	!====================================================!
	// isActive() => 활성화 여부를 체크하는 부분
	@Override
	public void run() {

		// console에서 넘겨 받은 애들이 종료가 안되고 잘 넘겨 받았는지 확인. client에서 다 close하지 않음
		refreshInfo();
		
		while(true) {
			System.out.println("Lobby.java/thread run method============================================================ Lobby.java에서 읽는 부분입니다.");
			try {
				Object obj = ois.readObject();
				if(obj instanceof ClientVO) {
					//	정보갱신
					System.out.println("정보갱신");
					vo = (ClientVO)obj;
					refreshInfo();
					
				}else if(obj instanceof String) {
					String readString = obj.toString().trim();
					System.out.println("읽은 명령 "+readString);

					String order = readString.substring(1, 6);	//	명령어
                    String value = readString.substring(7);	//	값
                    
                    System.out.println(order+":"+value);
                    
                    if(order.equals("COMNT")) {
//                    	대화
                    	chatArea.append(value+"\n");
    					JScrollBar jsb = chatBoard.getVerticalScrollBar();
    					jsb.setValue(jsb.getMaximum());
                    }else if(order.equals("ENTER")) {
//                    	방입장
                    	vo.setEnterRoomNo(Integer.parseInt(value));
                    	new Room(vo, Integer.parseInt(value), ois, oos);
                    	this.dispose();
                    	break;
                    }else if(order.equals("RUNRL")) {
//                    	룰렛
                    	vo.setCounter(Integer.parseInt(value));
                    }
//                    switch(order) {
//                    case "COMNT":
//                    //	대화
//                    	chatArea.append(value+"\n");
//    					JScrollBar jsb = chatBoard.getVerticalScrollBar();
//    					jsb.setValue(jsb.getMaximum());
//                    	break;
//                    case "ENTER":
//                    //	방입장
//                    	vo.setEnterRoomNo(Integer.parseInt(value));
//                    	new Room(vo, Integer.parseInt(value), ois, oos);
//                    	this.dispose();
//                    	break;
//                    case "RUNRL":
//                	//	룰렛
//                    	vo.setCounter(Integer.parseInt(value));
//                    	break;
//                    }
					
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				break;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "서버와의 접속이 끊겼습니다.");
				this.dispose();
				break;
			}
			
		}
		
//		while(!enterRoomState) { // 새로운 창이 떳을 때 active안됨.
//			try { // 실행시키면 에러 한번 발생. 이것은 while 도는 순서에 의해서 발생하는 부분
//				obj = ois.readObject();
//				System.out.println("Lobby.java/ run method에서 받는 obj : " + obj);
//
//				if(obj instanceof ClientVO) {
//					System.out.println("오브젝트 받음");
//					this.vo = (ClientVO) obj;
//					System.out.println("Lobby.java/ run method에서 받는 (ClientVO)vo : " + vo);
//					System.out.println("VO의 객체 정보를 확인 : "+vo.getOrderMsg() + "," + vo.getId() + "," + vo.getNickName() + "," + vo.getPoint());
//
//					if(vo.getOrderMsg().equals("enterLobby")){
//						System.out.println("VO의 객체 정보를 확인 : "+vo.getOrderMsg() + "," + vo.getId() + "," + vo.getNickName() + "," + vo.getPoint());
//						receiveInfo();
//						vo.setOrderMsg(""); // 처음 들어오면 orderMsg 한번 초기화
//					}else if(vo.getOrderMsg().equals("doneCreateRoom")) {
//						System.out.println("VO의 객체 정보를 확인 : "+vo.getOrderMsg() + "," + vo.getId() + "," + vo.getNickName() + "," + vo.getPoint());
//						System.out.println("소속된 room 넘버까지 받은 상태");
////						System.out.println(vo.getRoomIndex());
//						System.out.println("Lobby.java/ run method에서 받는 (ClientVO)vo : " + vo);
//						System.out.println("룸 클래스로 vo : " + vo + ", oos: " + oos + ", ois: " + ois);
//						new Room(s,vo,ois,oos);
//					}else{ // 룰렛 돌아왔을 때?
//						receiveInfo();
//					}
//				}else if (obj instanceof String) {
//					// 채팅창 로직
//					System.out.println("String 받음");
//					chatArea.append((String) obj);
//					System.out.println("obj: "+obj.toString());
//					JScrollBar jsb = chatBoard.getVerticalScrollBar();
//					jsb.setValue(jsb.getMaximum());
//				}
//
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			obj = null;
//		} // end while
	}

	private void refreshInfo() {
		System.out.println("정보 갱신 메소드 동작");
		lv = vo.getLevel();
		exp = vo.getExp();
		point = vo.getPoint();

		lvLabel.setText(""+lv);
		expLabel.setText(""+exp);
		pointLabel.setText(""+point);
	}

	//	배경화면 이미지 입히는 과정
	class BGPanel extends JPanel{
		Image img;
		public BGPanel(String src){
			img = Toolkit.getDefaultToolkit().createImage(src);
			setOpaque(false);
		}
		@Override
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, img.getWidth(this), img.getHeight(this), this);
			super.paint(g);
		}
	}

	//	새로고침
	private void requestRefrashInfo() {
		System.out.println("Lobby.java/ requestRefrashInfo method");
		try {
			oos.writeObject("/CLREF " + vo.getUserCode());
			oos.reset();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// 전체 채팅메시지
	private void sendMsg() {
		System.out.println("Lobby.java/ sendMsg method");
		try {
			System.out.println("Lobby.java/ sendMsg method 보내는 msg : " + chatField.getText());
			oos.writeObject("/COMNT "+name+" : "+chatField.getText()+"\n");
			oos.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//	�ｧ溢侃奓ｰ�ｳ��� scott ���圸 �ｻ､�ｧｨ�糖
//		if(userCode==1) {
//			if(msg.equals("/cmd count_reset")) {
//				dao.masterCountReset();
//				refreshInfo(vo);
//				msg="룰렛 카운터가 초기화되었습니다.";
//			}
//		}
		// �ｩ肥亨�ｧ� ��｡�侠
//		chatArea.append(name+" : " + msg + "\n");
		chatField.setText("");
		chatField.requestFocus();
		System.out.println("메시지 전송");
	}


	//!============= 여기서 실행되는 부분 ================!
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Lobby.java/actionPerformed method");
		if(e.getSource() == shutdownBtn) {
			new ClosePopup();
		}else if(e.getSource() == roomBtn) {
			if(point < 10) {
				JOptionPane.showMessageDialog(null, "포인트가 부족합니다.");
			}else {	
				try {
					oos.writeObject("/ENTER "+userCode);
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
//			new Room(s,vo,ois,oos);
		}else if(e.getSource() == helpBtn) {
			new Help();
		}else if(e.getSource() == rouletteBtn) {
			rl = new Roulette(vo.getUserCode(), vo.getPoint(), vo.getCounter(), vo.getNickName(),oos);
		}
	}


	//	!==========================================================!
	@Override
	public void windowActivated(WindowEvent e) {
		System.out.println("윈도우 활성화됨");
//		if(!(tr.isAlive())) tr.start();
		if(e.getSource() == this) {
			requestRefrashInfo();
		}
	}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}

	// 엔터를 쳐도 접속되게 하는 부분
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMsg();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}

}