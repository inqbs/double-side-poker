package gameProject;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import vo.ClientVO;

public class Client extends JFrame implements ActionListener, MouseListener, KeyListener, Runnable {
	String ip = "localhost";
	int port = 5000;

	Socket s;
	// for I/O
	BufferedReader br;
	PrintWriter pw;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	ClientVO client;

	Toolkit tool = Toolkit.getDefaultToolkit();

	JPanel loginPanel, signInPanel;
	JLabel bg;

	JLabel logo, idLabel, pwLabel;
	JTextField idTf;
	JPasswordField pwTf;
	JButton loginBtn, shutdownBtn, signUpBtn;

	ClientVO vo;
	static final int MAXLEN = 16;

	SignUp su;

	Client() {
		// GUI
		Dimension d = tool.getScreenSize();

		// window
		setLayout(null);
		setTitle("ONLINE DOUBLE POKER");
		setBounds((int) ((d.getWidth() - Style.WIDTH) / 2) - 1, (int) ((d.getHeight() - Style.HEIGHT) / 2) - 1,
				Style.WIDTH, Style.HEIGHT);
		// 1280*720
		this.setResizable(false);

		Style.setFont();

		bg = new JLabel(new ImageIcon("src/img/testbg.png"));
		bg.setBounds(0, 0, Style.WIDTH, Style.HEIGHT);

		logo = new JLabel(new ImageIcon("src/img/logo.png"));
		shutdownBtn = new JButton(new ImageIcon("src/img/btn_quit.png"));

		logo.setBounds((Style.WIDTH - 720) / 2, 100, 720, 200);

		shutdownBtn.setBounds(Style.WIDTH - 80, Style.HEIGHT / 100 * 85, 80, 50);
		shutdownBtn.setOpaque(false);
		shutdownBtn.setContentAreaFilled(false);
		shutdownBtn.setBorderPainted(false);

		signInPanel = new JPanel();
		signInPanel.setLayout(null);
		signInPanel.setBounds(Style.WIDTH / 3, Style.HEIGHT / 15 * 7, Style.WIDTH / 3, Style.HEIGHT / 3);
		// (426, 336, 426, 240)
		signInPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		signInPanel.setBackground(Style.mainColor);

		idLabel = new JLabel(new ImageIcon("src/img/label_id.png"));
		pwLabel = new JLabel(new ImageIcon("src/img/label_pw.png"));

		idTf = new JTextField(16);
		pwTf = new JPasswordField(16);
		pwTf.setEchoChar('●');

		signUpBtn = new JButton(new ImageIcon("src/img/btn_gray.png"));
		signUpBtn.setOpaque(false);
		signUpBtn.setContentAreaFilled(false);
		signUpBtn.setBorderPainted(false);
		loginBtn = new JButton(new ImageIcon("src/img/btn_orange.png"));
		loginBtn.setOpaque(false);
		loginBtn.setContentAreaFilled(false);
		loginBtn.setBorderPainted(false);

		idLabel.setBounds(Style.MARGIN_IN_SIGNIN, Style.MARGIN_IN_SIGNIN, signInPanel.getWidth() / 5 - 6, 36);
		idLabel.setFont(Style.subFont);
		idLabel.setForeground(Color.WHITE);
		pwLabel.setBounds(Style.MARGIN_IN_SIGNIN, Style.MARGIN_IN_SIGNIN + idLabel.getHeight() + 20,
				signInPanel.getWidth() / 5 - 6, 36);
		pwLabel.setFont(Style.subFont);
		pwLabel.setForeground(Color.WHITE);

		idTf.setBounds(Style.MARGIN_IN_SIGNIN * 2 + pwLabel.getWidth(), Style.MARGIN_IN_SIGNIN,
				signInPanel.getWidth() / 2, 36);
		idTf.setFont(Style.subFont);
		pwTf.setBounds(Style.MARGIN_IN_SIGNIN * 2 + pwLabel.getWidth(),
				Style.MARGIN_IN_SIGNIN + idLabel.getHeight() + 20, signInPanel.getWidth() / 2, 36);
		pwTf.setFont(Style.subFont);

		signUpBtn.setBounds(40, 152, (signInPanel.getWidth() - 100) / 2, 60);
		signUpBtn.setFont(Style.mainFont);
		signUpBtn.setFocusPainted(false);
		loginBtn.setBounds(213, 152, (signInPanel.getWidth() - 100) / 2, 60);
		loginBtn.setFont(Style.mainFont);
		loginBtn.setFocusPainted(false);

		loginBtn.addActionListener(this);
		loginBtn.addMouseListener(this);
		signUpBtn.addActionListener(this);
		signUpBtn.addMouseListener(this);
		shutdownBtn.addActionListener(this);

		idTf.addKeyListener(this);
		pwTf.addKeyListener(this);

		signInPanel.add(idLabel);
		signInPanel.add(pwLabel);
		signInPanel.add(idTf);
		signInPanel.add(pwTf);
		signInPanel.add(loginBtn);
		signInPanel.add(signUpBtn);

		add(logo);
		add(signInPanel);
		add(shutdownBtn);
		add(bg);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
	} // end constructor

	public static void main(String[] args) {
//		new Client();
		Thread th = new Thread(new Client());
		th.start();
	}


	@Override
	public void run() {
		System.out.println("Client.java/thread run method============================================================ Client.java에서 읽는 부분입니다.");
		
		//	서버와 접속 시도
			try {
				s = new Socket(ip, port);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				System.out.println("Client.java : "+ois);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		while(true) {
			try {
				Object obj = ois.readObject();
				
				if(obj instanceof String) {
						
					 String order = obj.toString().substring(1, 6);	//	명령어
	                 String value = obj.toString().substring(7);	//	값
	                 System.out.println(obj.toString());
	                 
					if(order.equals("CHECK")) {
						su.setAlwaysOnTop(false);
						switch(value) {
						case "true":
							JOptionPane.showMessageDialog(null, "사용 가능한 아이디 입니다..");
							break;
						case "false":
							JOptionPane.showMessageDialog(null, "중복된 아이디 입니다..");
							break;
						}
						su.setAlwaysOnTop(true);
					}else {
						//	회원가입 처리부
						su.setAlwaysOnTop(false);
						if(value.equals("true")) {
							JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다. ");
							su.dispose();
						}else {
							JOptionPane.showMessageDialog(null, "서버 오류가 발생했습니다. 다시 시도해주세요");
						}
						su.setAlwaysOnTop(true);
					}
				
				}else if(obj instanceof ClientVO) {
					vo = (ClientVO) obj;
					if(vo!=null) {
						JOptionPane.showMessageDialog(null, "로그인에 성공하셨습니다.");
						new Lobby(s, vo, oos, ois);
						this.dispose();
						break;
					}else {
						JOptionPane.showMessageDialog(null, "접속에 실패하였습니다..");
					}
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
	} // end run method

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Client.java/actionPerformed method");
		if (e.getSource().equals(shutdownBtn)) {
			new ClosePopup();
		} else if (e.getSource() == signUpBtn) {
			su = new SignUp(oos);
			System.out.println("회원가입 켜질 시점 : "+ois);
		} else if (e.getSource() == loginBtn) {
			//	로그인
			login();
		}
	}

	private void login() {
		System.out.println("Client.java/login method");
		String userid = idTf.getText().trim();

		StringBuffer pwTmp = new StringBuffer();
		for (char x : pwTf.getPassword())
			pwTmp.append(x);
		String userpw = pwTmp.toString().trim();

		if (userid.isEmpty() || userpw.isEmpty()) {
			JOptionPane.showMessageDialog(null, "");
		} else {
			// 보내는 부분
			try {
				oos.writeObject("/LOGIN "+ userid + "," + userpw);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == signUpBtn) {
			signUpBtn.setIcon(new ImageIcon("src/img/btn_gray_clicked.png"));
		} else if (e.getSource() == loginBtn) {
			loginBtn.setIcon(new ImageIcon("src/img/btn_orange_clicked.png"));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == signUpBtn) {
			signUpBtn.setIcon(new ImageIcon("src/img/btn_gray.png"));
		} else if (e.getSource() == loginBtn) {
			loginBtn.setIcon(new ImageIcon("src/img/btn_orange.png"));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == idTf || e.getSource() == pwTf) {
			JTextField field = (JTextField) e.getSource();
			String str = field.getText();
			if (str.length() >= MAXLEN) {
				str = str.substring(0, MAXLEN);
				field.setText(str);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			login();
		}
	}

}