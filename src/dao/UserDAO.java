package dao;

import java.sql.*;

import gameProject.Client;
import vo.ClientVO;

public class UserDAO {

    // �???�� ?��?��
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@orcl.csdgsmpvrcxc.ap-northeast-2.rds.amazonaws.com:1521:orcl";
    String user = "scott";
    String password = "tigertiger";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer();

    public UserDAO(){
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("드라이버 로딩");

        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패");
            e.printStackTrace();
        }catch (SQLException e) {
            System.out.println("db 연결 실패");
            e.printStackTrace();
        }
    }
    
    
    //	로그?��
    public ClientVO login(String Id, String Pw) {
		sb.setLength(0);
		sb.append("SELECT user_code, nickname, lv, exp, point, count from user_list ");
		sb.append("WHERE ID = ? ");
		sb.append("and PW = ? ");

		ClientVO vo = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());
			System.out.println(pstmt);
			pstmt.setString(1, Id);
			pstmt.setString(2, Pw);
			rs = pstmt.executeQuery();
			if(rs.next()){
				vo = new ClientVO();
				vo.setUserCode(rs.getInt("user_code"));
				vo.setNickName(rs.getString("nickname"));
			    vo.setLevel(rs.getInt("lv"));
			    vo.setExp(rs.getInt("exp"));
			    vo.setPoint(rs.getInt("point"));
			    vo.setCounter(rs.getInt("count"));
			}
//			if(rs.next()){	//	?��?�� ?��?�� ?��?���?
//				vo = new UserVO();
//			   vo.setUserCode(rs.getInt("user_code"));
//			   vo.setName(rs.getString("nickname"));
//			   vo.setLv(rs.getInt("lv"));
//			   vo.setExp(rs.getInt("exp"));
//			   vo.setPoint(rs.getInt("point"));
//			   vo.setCount(rs.getInt("count"));
//			}else{
//				System.out.println("ID ?��?? PW�? ?��릅니?��.");
//				vo = null;
//			}
			
		} catch (SQLException e) {
			System.out.println("ID ?��?? PW�? ?��릅니?��.");
			//vo = null;
			e.printStackTrace();
		}
		
		return vo;
	}

    //	?��?���??��
	public boolean registratorUser(ClientVO vo) {
		sb.setLength(0);
		sb.append("INSERT INTO USER_LIST ");
		sb.append("(USER_CODE, ID, PW, NICKNAME, PHONE, EMAIL, COUNT) ");
		sb.append("VALUES (USER_USER_CODE_SEQ.NEXTVAL, ?, ?, ?, ?, ? ,3) ");
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getPw());
			pstmt.setString(3, vo.getNickName());
			pstmt.setString(4, vo.getPhone());
			pstmt.setString(5, vo.getEmail());
			
			pstmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			System.out.println("회원가입 실패");
			return false;
		}

	}
	
//	id중복체크
	public boolean selectIdCheck(String Id) {
		boolean flag = false;
		sb.append("SELECT id from user_list ");
		sb.append("WHERE ID = ? ");
		ClientVO vo = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, Id);
			rs = pstmt.executeQuery();
			
			if(!(rs.next())){	//	?��?�� ?��?�� ?��?���?
				flag = true;
			}
		} catch (SQLException e) {
			System.out.println("찾을?�� ?��?��?��?��.");
			e.printStackTrace();
		}
		return flag;	
	}
	
	//	?��???���? 갱신
	public ClientVO refresh(int userCode) {
		sb.setLength(0);
		sb.append("SELECT lv, exp, point, count from user_list ");
		sb.append("WHERE user_code = ? ");
		
		ClientVO vo = new ClientVO();
		
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, userCode);
			rs = pstmt.executeQuery();
			
			
			if(rs.next()){	//	?��?�� ?��?�� ?��?���?
				vo.setLevel(rs.getInt("lv"));
				vo.setExp(rs.getInt("exp"));
				vo.setPoint(rs.getInt("point"));
				vo.setCounter(rs.getInt("count"));
			}else{	//	?��?��?��?�� ?��?�� == 결과�? ?��?�� == ?��?��?�� 비번?�� ?��르다.
				System.out.println("아이디 혹은 PW가 다릅니다.");
				vo = null;
			}
			
		} catch (SQLException e) {
			System.out.println("?��류입?��?��");
			vo = null;
			e.printStackTrace();
		}
		
		return vo;
	}
	
	//	룰렛?�� ?��?��?��
	public int updatepoint(int userCode, int point, int count) {
		sb.setLength(0);
		sb.append("UPDATE USER_LIST ");
		sb.append("SET POINT = ?, ");
		sb.append("COUNT = ? ");
		sb.append("WHERE user_code = ? ");
		int cnt = count;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, point);
			if(cnt>0)
				cnt--;
			pstmt.setInt(2, cnt);
			pstmt.setInt(3, userCode);
			pstmt.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cnt;
	}
	
	//	?��?��?�� 종료?�� ?��?�� ?��?��?��?��
	
	//	?��버단
	
	
	//	?��?��?��?��?��?��
	public void updateGameResultInClient(int userCode, int lv, int exp, int point, int result) {
		sb.setLength(0);
		int maxExp = 0;
				
		int userNo = userCode;
		int userLv = lv;
		int userExp = exp;
		int userPoint = point;
		
		try {
			//	최�?경험치�?? get
			pstmt = conn.prepareStatement("SELECT exp from EXP_GRADE where lv=?");
			//	SELECT exp from EXP_GRADE where lv = ?;  // 최�? 경험�? �??��?�� select �?
			pstmt.setInt(1, lv);
			rs = pstmt.executeQuery();
			if(rs.next())	//	?��?�� ?��?�� ?��?���?
			   maxExp = rs.getInt(1);	//	select?�� exp�? maxexp
			
			
			//	경기 결과?�� ?��?�� 경험치배�?
			
			switch(result) {
			case 1:	//	?���?
				userExp += 15;
				break;
			case 2:	//	?���?
				userExp += 3;
				break;
			case 3:	//	무승�?
				userExp += 8;
				break;
			}
			
			//	경험�? 배분 결과 ???��
			sb.setLength(0);
			sb.append("Update USER_LIST ");
			sb.append("set lv = ? , exp = ? ");
			sb.append("where user_code = ? ");
			pstmt = conn.prepareStatement(sb.toString());
			//	?��벨업 로직 (?��?�� 경험치�? ?��?���?�?)
			if(userExp>maxExp) {
				userLv++;
			}
			pstmt.setInt(1, userLv);
			pstmt.setInt(2, userExp);
			pstmt.setInt(3, userNo);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	//	?��?�� 룰렛 카운?�� 초기?��
	//	?��?��마다 1번씩 ?��?��?��?��?�� �??�� (?��?���? 구현 x)
	
	//	마스?��계정 scott?�� ?��?��?�� 코드
	public void masterCountReset() {
		sb.setLength(0);
		sb.append("Update USER_LIST ");
		sb.append("set count = 9 ");
		sb.append("where user_code = 1 ");
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
