package gameProject;


import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Help extends JFrame implements MouseListener {

	BGPanel bgPanel;
	JPanel cardPanel;
	JButton prevBtn, nextBtn;
	ImageIcon help1, help2, help3, help4;
	BGPanel[] helpPanel;
	
	CardLayout layout;
	int panelCount;

	Help() {
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		
		setLayout(null);
		setBounds((int)((d.getWidth()-700)/2), (int)((d.getHeight()-619))/2,685, 640);
		
		//	배경이미지 패널
		bgPanel = new BGPanel("src/img/bg_help.png");
		bgPanel.setOpaque(false);
		bgPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		bgPanel.setLayout(null);
		
		//	도움말 패널
		cardPanel = new JPanel();
		layout = new CardLayout();
		cardPanel.setLayout(layout);
		cardPanel.setBounds(0, 0, 685, 560);
		
		helpPanel = new BGPanel[4];
		for(int i=0; i<helpPanel.length; i++) {
			helpPanel[i] = new BGPanel("src/img/"+(i+1)+".png");
			cardPanel.add(helpPanel[i]);
		}
		
		bgPanel.add(cardPanel);
		
		prevBtn = new JButton(new ImageIcon("src/img/btn_help_prev.png"));
		nextBtn = new JButton(new ImageIcon("src/img/btn_help_next.png"));
		
		prevBtn.setBounds(0, 580, 120, 40);
		nextBtn.setBounds(565, 580, 120, 40);
		
		prevBtn.setContentAreaFilled(false);
		prevBtn.setBorderPainted(false);
		prevBtn.setFocusPainted(false);
		nextBtn.setContentAreaFilled(false);
		nextBtn.setBorderPainted(false);
		nextBtn.setFocusPainted(false);
		
		prevBtn.addMouseListener(this);
		nextBtn.addMouseListener(this);
		
		prevBtn.setEnabled(false);
		
		bgPanel.add(prevBtn);
		bgPanel.add(nextBtn);
		
		
	

//		btn1 = new JButton("도움말1");
//		btn2 = new JButton("도움말2");
//		btn3 = new JButton("도움말3");
//		btn4 = new JButton("도움말4");
//		exit = new JButton("도움말 종료");
//
//		btn1.setBounds(45, 0, 102, 20);
//		btn2.setBounds(167, 0, 102, 20);
//		btn3.setBounds(289, 0, 102, 20);
//		btn4.setBounds(411, 0, 102, 20);
//		exit.setBounds(533, 0, 102, 20);

//		bg.setIcon(help1);
//		bg.setBounds(0, 20, 685, 560);

		add(bgPanel);

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setUndecorated(true);
		setBackground(new Color(0,0,0,0));
		setVisible(true);
		
		panelCount = 0;
	}
	
	public static void main(String[] args) {
		Help help = new Help();
	}// main method end
	
//	배경이미지를 패널에 출력하기 위한 class BGPanel
	class BGPanel extends JPanel{
		Image img;

		public BGPanel(String src){
			img = Toolkit.getDefaultToolkit().createImage(src);
			setOpaque(false);
		}
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(img, 0, 0, img.getWidth(this), img.getHeight(this), this);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == prevBtn && panelCount>0) {
			layout.previous(cardPanel);
			panelCount--;
		}else if(e.getSource() == nextBtn) {
			if(panelCount+1 == helpPanel.length) {
				this.dispose();
			}else {
				layout.next(cardPanel);
				panelCount++;
			}
		}
		switch(panelCount) {
		case 0:
			prevBtn.setEnabled(false);
			break;
		case 1:
			prevBtn.setEnabled(true);
			break;
		case 3:
			nextBtn.setIcon(new ImageIcon("src/img/btn_help_exit.png"));
			break;
		default:
			nextBtn.setIcon(new ImageIcon("src/img/btn_help_next.png"));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}// class end