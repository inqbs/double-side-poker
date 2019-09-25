package gameProject;

import javax.swing.JOptionPane;

public class ClosePopup {
	
	int result;
	
	ClosePopup(){
		result = JOptionPane.showConfirmDialog(null, "종료하시겠습니까? ", "확인", JOptionPane.OK_CANCEL_OPTION);
		switch(result) {
		case JOptionPane.CANCEL_OPTION:
			break;
		case JOptionPane.OK_OPTION:
			System.exit(0);
			break;
		}
	}
	
}
