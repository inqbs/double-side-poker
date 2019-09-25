package gameProject;
import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

//	��Ÿ���� �����ϱ� ���� static����� ����

public class Style {
	
	
	//	â ������
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	//	margin
	public static final int MARGIN = 40;
	public static final int MARGIN_IN_SIGNIN = 40;
	public static final Border PADDING = new EmptyBorder(20, 20, 20, 20);
	
	//	�÷�
	public static final Color mainColor = new Color(60,4,4);
	public static final Color subColor = new Color(232, 206, 179);
	public static final Color bgColor = new Color(142,67,67);
	
	public static final Color notColor = new Color(1,1,1,0);
	
//	URL fontUrl = new URL("font/") 
	
	//	�۲�
	public static final Font infoFont = new Font("맑은 고딕", Font.BOLD, 20);
	public static final Font mainFont = new Font("맑은 고딕", Font.BOLD, 16);
	public static final Font subFont = new Font("맑은 고딕", Font.PLAIN, 12);
	
	public static void setFont(){
		UIManager.put("JButton.font",Style.mainFont); 
		UIManager.put("JLabel.font",Style.mainFont);
		UIManager.put("JTextField.font",Style.mainFont);
		UIManager.put("JTextArea.font",Style.mainFont);
		UIManager.put("JPassword.font",Style.mainFont);
	}
	
}
