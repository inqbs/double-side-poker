package gameProject;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import vo.ClientVO;

public class SignUp extends JFrame implements ActionListener, MouseListener, WindowListener{

	JPanel header,body, footer, marginL, marginR, windowPanel;
	JLabel headLabel, idLabel, nameLabel, pwLabel, emailLabel, phoneLabel;
	JTextField idTf, nameTf, emailTf, phoneTf;
	JPasswordField pwTf;
	JButton idDupBtn, confirmBtn, cancelBtn;
	JCheckBox jck;
	GridBagLayout gbl = new GridBagLayout();
	JOptionPane jop;
	Toolkit tk = Toolkit.getDefaultToolkit();
	// ClientVO 정보를 저장할 멤버변수 vo
	//	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Thread tr;

	public SignUp() {
		setBounds(Style.WIDTH/2,Style.HEIGHT/3,
				Style.WIDTH/4,Style.HEIGHT/3*2);
		this.setResizable(false);
		this.setUndecorated(true);
		this.setShape(new RoundRectangle2D.Float(0,0, this.getWidth(),this.getHeight(), 32,32));

		Style.setFont();

		windowPanel = new JPanel();
		windowPanel.setBounds(0,0, this.getWidth(), this.getHeight());
		windowPanel.setLayout(new BorderLayout(30,25));

		Image tfBg = tk.getImage("src/img/text.field.png");

		header = new JPanel();
		body = new JPanel();
		footer = new JPanel();
		marginL = new JPanel();
		marginR = new JPanel();

		header.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		headLabel = new JLabel(new ImageIcon("src/img/label_signup_header.png"));
		header.setBackground(Style.mainColor);

		body.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		footer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		footer.setBackground(Style.mainColor);


		//	body
		idLabel = new JLabel(new ImageIcon("src/img/label_id_small.png"));
		nameLabel = new JLabel(new ImageIcon("src/img/label_name_small.png"));
		pwLabel = new JLabel(new ImageIcon("src/img/label_pw_small.png"));
		emailLabel = new JLabel(new ImageIcon("src/img/label_email_small.png"));
		phoneLabel = new JLabel(new ImageIcon("src/img/label_phone_small.png"));
		jck = new JCheckBox("동의?");

		idTf = new JTextField(16);
		idDupBtn = new JButton("체크");
		pwTf = new JPasswordField(16);
		nameTf = new JTextField(16);
		emailTf = new JTextField(60);
		phoneTf = new JTextField(16);

		idLabel.setFont(Style.subFont);
		nameLabel.setFont(Style.subFont);
		pwLabel.setFont(Style.subFont);
		emailLabel.setFont(Style.subFont);
		phoneLabel.setFont(Style.subFont);
		pwTf.setEchoChar('*');

		//	gridBackLayout
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 6;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbl.setConstraints(idLabel, gbc);
		gbc.gridy = 1;
		gbl.setConstraints(nameLabel, gbc);
		gbc.gridy = 2;
		gbl.setConstraints(pwLabel, gbc);
		gbc.gridy = 3;
		gbl.setConstraints(emailLabel, gbc);
		gbc.gridy = 4;
		gbl.setConstraints(phoneLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = 2;
		gbc.gridy = 5;
		gbl.setConstraints(jck, gbc);

		gbc.gridwidth = 1;
		gbc.weightx = 0.5f;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbl.setConstraints(idTf, gbc);
		gbc.gridwidth = 2;
		gbc.gridy = 1;
		gbl.setConstraints(nameTf, gbc);
		gbc.gridy = 2;
		gbl.setConstraints(pwTf, gbc);
		gbc.gridy = 3;
		gbl.setConstraints(emailTf, gbc);
		gbc.gridy = 4;
		gbl.setConstraints(phoneTf, gbc);

		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbl.setConstraints(idDupBtn, gbc);

		//	footer
		confirmBtn = new JButton(new ImageIcon("src/img/btn_signup.png"));
		confirmBtn.setOpaque(false);
		confirmBtn.setContentAreaFilled(false);
		confirmBtn.setBorderPainted(false);
		confirmBtn.setFocusPainted(false);

		cancelBtn = new JButton(new ImageIcon("src/img/btn_cancel.png"));
		cancelBtn.setOpaque(false);
		cancelBtn.setContentAreaFilled(false);
		cancelBtn.setBorderPainted(false);
		cancelBtn.setFocusPainted(false);

		idDupBtn.addActionListener(this);
		confirmBtn.addActionListener(this);
		confirmBtn.addMouseListener(this);
		cancelBtn.addActionListener(this);
		cancelBtn.addMouseListener(this);

		header.add(headLabel);
		body.add(idLabel);
		body.add(idTf);
		body.add(idDupBtn);
		body.add(pwLabel);
		body.add(pwTf);
		body.add(nameLabel);
		body.add(nameTf);
		body.add(emailLabel);
		body.add(emailTf);
		body.add(phoneLabel);
		body.add(phoneTf);
		body.add(jck);

		footer.add(confirmBtn);
		footer.add(cancelBtn);

		windowPanel.add(header,"North");
		windowPanel.add(body,"Center");
		windowPanel.add(footer,"South");
		windowPanel.add(marginL,"West");
		windowPanel.add(marginR,"East");

		add(windowPanel);

		windowPanel.setBorder(new LineBorder(Style.mainColor, 8, true));

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setVisible(true);

		jop = new JOptionPane();
	} // 기본 생성자 -> 이미지 관련

	SignUp(ObjectOutputStream oos){
		this();
		this.oos = oos;
	}

	private void createAccount() {
		System.out.println("SingUp.java/createAccount method");
		String id = idTf.getText();
		StringBuffer pwTmp = new StringBuffer();
		for(char x: pwTf.getPassword())
			pwTmp.append(x);
		String pw = pwTmp.toString();
		String name = nameTf.getText();
		String email = emailTf.getText();
		String phone = phoneTf.getText();

		if(id.isEmpty()||pw.isEmpty()||name.isEmpty()||email.isEmpty()||phone.isEmpty()) {
			this.setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null, "칸을 다 채워 주세요");
			this.setAlwaysOnTop(true);
		}else if(!(jck.isSelected())) {
			this.setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null, "동의 버튼을 체크해 주세요");
			this.setAlwaysOnTop(true);
		}else {
			System.out.println("SingUp.java/createAccount method : 회원가입 준비");

			try {
				System.out.println("SingUp.java/createAccount method : 가입정보전송");
				oos.writeObject("/SGNUP "+id+","+pw+","+name+","+email+","+phone);
				oos.reset();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}


	} // end create account
	
	// !===========================================================!
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("SingUp.java/actionPerformed method");
		if(e.getSource() == cancelBtn) {
			this.dispose();
		}else if(e.getSource() == confirmBtn) {
			System.out.println("confirmBtn");
			createAccount();
		}else if(e.getSource() == idDupBtn) {
			String userid = idTf.getText().trim();
			try {
				oos.writeObject("/CHECK "+userid);
				oos.reset();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == confirmBtn) {
			confirmBtn.setIcon(new ImageIcon("src/img/btn_signup_clicked.png"));
		}else if(e.getSource() == cancelBtn) {
			cancelBtn.setIcon(new ImageIcon("src/img/btn_cancel_clicked.png"));
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == confirmBtn) {
			confirmBtn.setIcon(new ImageIcon("src/img/btn_signup.png"));
		}else if(e.getSource() == cancelBtn) {
			cancelBtn.setIcon(new ImageIcon("src/img/btn_cancel.png"));
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			if(oos!=null) oos.close();
			if(ois!=null) ois.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// 안쓰는 상속받은 메서드들
	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}


}
