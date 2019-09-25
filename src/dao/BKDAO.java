package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import vo.BKVO;

public class BKDAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@192.168.0.36:1521:orcl";
	String user = "scott";
	String password = "tiger";
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	StringBuffer sb = new StringBuffer();
	Scanner sc = new Scanner(System.in);

	public BKDAO() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connection Success");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Loading Failure ");
		} catch (SQLException e) {
			System.out.println("DB Connection Failure ");
			e.printStackTrace();
		}
	}

	public ArrayList<BKVO> selectAll() {
		ArrayList<BKVO> list = new ArrayList<BKVO>();
		sb.setLength(0);
		sb.append("SELECT * FROM dept");
		try {
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String Id = rs.getString("id");
				String Pw = rs.getString("pw");
				String Name = rs.getString("name");
				String Email = rs.getString("email");
				String Phone = rs.getString("phone");
				BKVO vo = new BKVO();
				vo.setId(Id);
				vo.setPw(Pw);
				vo.setName(Name);
				vo.setEmail(Email);
				vo.setPhone(Phone);
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public BKVO select(String Id) {
		sb.append("SELECT * FROM user ");
		sb.append("WHERE ID = ? ");
		BKVO vo = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, Id);
			rs = pstmt.executeQuery();
			rs.next();
			vo = new BKVO();
			vo.setId(Id);
		} catch (SQLException e) {
			System.out.println("찾을수 없습니다.");
			e.printStackTrace();
		}
		return vo;
	}

	public int updatepoint(String Id, int point) {
		sb.setLength(0);
		sb.append("UPDATE user SET point = ? ");
		sb.append("WHERE ID = ? ");
		int result = 0;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, point);
			pstmt.setString(2, Id);
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;

	}

	public BKVO selectOne(String Id, String Pw) {
		sb.setLength(0);
		sb.append("SELECT * FROM user ");
		sb.append("WHERE ID = ? ");
		sb.append("and PW = ? ");

		BKVO vo = null;

		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, Id);
			pstmt.setString(2, Pw);
			rs = pstmt.executeQuery();
			rs.next();
			vo = new BKVO();
			vo.setId(Id);
			vo.setPw(Pw);

		} catch (SQLException e) {
			System.out.println("찾을수 없습니다.");
			e.printStackTrace();
		}

		return vo;
	}

	public void insertOne(BKVO vo) {
		sb.setLength(0);
		sb.append("INSERT INTO user ");
		sb.append("VALUES (?, ?, ?, ?, ? )");
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getName());
			pstmt.setString(3, vo.getPw());
			pstmt.setString(4, vo.getPhone());
			pstmt.setString(5, vo.getEmail());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
