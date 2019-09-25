package gameProject;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import vo.ClientVO;

public class Roulette extends JFrame implements ActionListener, Runnable {

	JLabel bg;
	JButton startBtn, exitBtn;
	JLabel pointLabel, countLabel;

	int userCode, count, point, value ,tmp;
	String name;

	Toolkit tool = Toolkit.getDefaultToolkit();

	Boolean running;
	ImageIcon defIcon;

	ObjectOutputStream oos;
	ObjectInputStream ois;

	ClientVO vo;

	Roulette() {
		Dimension d = tool.getScreenSize();

		setLayout(null);
		setTitle("행운의 룰렛");
		setBounds((int)(d.getWidth()/2-240), (int)(d.getHeight()/2-240), 480, 480);

		bg = new JLabel();
		pointLabel = new JLabel();
		countLabel = new JLabel();
		startBtn = new JButton();
		exitBtn = new JButton(new ImageIcon("src/img/btn_roulet_exit.png"));

		bg.setBounds(0, 0, this.getWidth(), this.getHeight());
		bg.setIcon(new ImageIcon("src/img/bg_rullet.png"));

		pointLabel.setBounds(90,20, 150, 30);
		pointLabel.setHorizontalAlignment(JLabel.CENTER);
		pointLabel.setFont(Style.infoFont);
		pointLabel.setForeground(Color.white);

		countLabel.setBounds(280, 20, 120, 30);
		countLabel.setHorizontalAlignment(JLabel.CENTER);
		countLabel.setFont(Style.infoFont);
		countLabel.setForeground(Color.white);

		startBtn.setBounds(80, 80, 320, 320);
		startBtn.setOpaque(false);
		startBtn.setContentAreaFilled(false);
		startBtn.setBorderPainted(false);
		startBtn.setFocusPainted(false);

		defIcon = new ImageIcon("src/img/rullet.png");
		startBtn.setIcon(defIcon);

		exitBtn.setBounds((getWidth()-100)/2, getHeight()-70 , 100, 32);
		exitBtn.setOpaque(false);
		exitBtn.setContentAreaFilled(false);
		exitBtn.setBorderPainted(false);
		exitBtn.setFocusPainted(false);

		startBtn.addActionListener(this);
		exitBtn.addActionListener(this);

		add(pointLabel);
		add(countLabel);
		add(startBtn);
		add(exitBtn);
		add(bg);

		setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		running = false;
	}

//	Roulette(int userCode, ObjectOutputStream oos, ObjectInputStream ois){
//		this();
//		this.userCode = userCode;
//		this.oos = oos;
//		this.ois = ois;
//	}

	Roulette(int userCode, int point, int count, String name){
		this();
		this.userCode = userCode;
		this.count = count;
		this.point = point;
		this.name = name;

		countLabelSetText();
	}

	public Roulette(int userCode, int point, int count, String name, ObjectOutputStream oos) {
		this(userCode, point, count, name);
		this.oos = oos;
	}

	public static void main(String[] args) {
		new Roulette(1, 0, 500, "킹갓제네럴");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startBtn) {
			if(!running) run();
		}else if(e.getSource() == exitBtn) {
			this.dispose();	//	창 닫기
		}
	}


	@Override
	public void run() {
		running = true;
		Random rnd = new Random();
		value = rnd.nextInt(200);

//		ImageIcon ic = null;

		if (point<10) {	//	포인트가 10보다 적을때 기회 (꽝 없음)
			if(value<4) {
				point += (tmp=500);
			}else if (value < 14) {
				point += (tmp=100);
			}else if (value < 30) {
				point += (tmp=50);
			}else {
				point += (tmp=30);
			}
			setImage(tmp);
			updatePoint();
		}else if(count>0) {	//	통상 가챠
			if(value<100) {
				if(value<4) {
					point += (tmp=500);
				}else if (value < 14) {
					point += (tmp=100);
				}else if (value < 30) {
					point += (tmp=50);
				}else {
					point += (tmp=30);
				}
				setImage(tmp);
			}else{
				setImage(0);
			}
			updatePoint();
		}else {
			JOptionPane.showMessageDialog(null, "오늘 돌릴수 있는 룰렛을 모두 돌리셨습니다.");
		}

		tmp=0;
	}

	//	룰렛 돌아가는 이미지 설정
	private void setImage(int tmp) {
//		BufferedImage ic;
//			File f = new File("src/img/rullet/rullet_"+tmp+".gif");
//			System.out.println(f.getAbsoluteFile());
//			ic = ImageIO.read(f);
		System.out.println(tmp);
		startBtn.setIcon(new ImageIcon("src/img/rullet/rullet_"+tmp+".gif"));
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(tmp>0)
					JOptionPane.showMessageDialog(null, "축하합니다! "+tmp+" 포인트를 획득하셨습니다!");
				else
					JOptionPane.showMessageDialog(null, "아쉽습니다! 기회를 놓치셨군요...");
				running = false;
				startBtn.setIcon(defIcon);

//				sendResult(){
//					oos.writeObject(obj);
//				};
				countLabelSetText();
			}
		};
		new Timer().schedule(task, 7777);
	}

	//	카운트, 포인트 설정
	private void countLabelSetText(){
		pointLabel.setText(point+"");
		if(this.point<10) {
			countLabel.setText("");
			countLabel.setIcon(new ImageIcon("src/img/label_FreeNow.png"));
		}else {
			countLabel.setText(count+"");
			countLabel.setIcon(null);
		}
	}

	private void updatePoint() {

		if(count>0) count--;	//	카운터 -1
		try {
			oos.writeObject("/RUNRL "+userCode+","+point+","+count);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

