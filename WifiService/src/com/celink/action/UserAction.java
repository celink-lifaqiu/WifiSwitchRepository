package com.celink.action;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.celink.action.pojo.User;
import com.celink.jdbc.JDBCConnection;
import com.celink.util.JsonUtil;

public class UserAction {
	public static int addUser(User user){
		int code = 1;
		Connection connection = JDBCConnection.getConnection();
		if(connection == null){
			return 500;
		}
		PreparedStatement pstmt = null;
		try {
			String sql = "insert into user_tb(email,pwd,nickName) values(?,?,?)";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, getMD5Password(user.getPwd(),user.getEmail()));
			pstmt.setString(3, user.getNickName());
			int rs = pstmt.executeUpdate();
			if (rs > 0) {
				code = 0;
			}
			
		} catch (SQLException e) {
			code = 4;
		} finally{
			try {
				if(pstmt != null){
					pstmt.close();
				}
				if(connection != null){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return code;
	}
	
	private static String getMD5Password(String password, String account) {
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		return encoder.encodePassword(password, account);
	}

	public static User findUserByEmail(String email) {
		User user = null;
		Connection connection = JDBCConnection.getConnection();
		if(connection == null){
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			String sql = "select * from user_tb where email=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if(rs.next()){
				user = new User();
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setNickName(rs.getString("nickName"));
				user.setPwd(rs.getString("pwd"));
			}
		}catch (SQLException e) {
			
		}finally{
			try {
				if(pstmt != null){
					pstmt.close();
				}
				if(rs != null){
					rs.close();
				}
				if(connection != null){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}
	
	public static User findUserByEmail(String email, String pwd) {
		User user = null;
		Connection connection = JDBCConnection.getConnection();
		if(connection == null){
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			String sql = "select * from user_tb where email=? and pwd=?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, getMD5Password(pwd, email));
			rs = pstmt.executeQuery();
			if(rs.next()){
				user = new User();
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setNickName(rs.getString("nickName"));
				user.setPwd(rs.getString("pwd"));
			}
		}catch (SQLException e) {
			
		}finally{
			try {
				if(pstmt != null){
					pstmt.close();
				}
				if(rs != null){
					rs.close();
				}
				if(connection != null){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}
	
}
