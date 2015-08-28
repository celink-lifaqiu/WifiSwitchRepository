package com.celink.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginContext;

import com.celink.server.JsonUtil;

public class Server {
	static Map<String, Socket> socketMap = new HashMap<String, Socket>();
	static List<Socket> list = new ArrayList<Socket>();
	public static int PORT = 8000;
	public static String HOST = "127.0.0.1";
	private static int client_num = 0;
	// static Scanner scanner;
	static PrintWriter pw;

	private static Server server;

	private Server() {

	}

	public static Server getInstance() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(PORT);
			// 绛夊緟璇锋眰,鏃犺姹備竴鐩寸瓑寰�
			new ThreadSendmsg();
			while (true) {
				System.out.println("Waiting Client");
				socket = serverSocket.accept();// 鎺ュ彈璇锋眰
				System.out.println("Client Conect!");
				System.out.println(socket);
				socketMap.put(String.valueOf(socket.getPort()), socket);
				list.add(socket);
				client_num++;
				System.out.println(socketMap.size());
				new ThreadedServer(socket, socketMap);
				new ThreadSendmsg();
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static class ThreadSendmsg extends Thread {

		public ThreadSendmsg() {
			start();
		}

		@Override
		public void run() {
			String message = "";
		//	int command = 0;
			Scanner scanner = new Scanner(System.in);
			while (true) {
				try {
					System.out.println("请选择发送消息的socket：");
					message = scanner.nextLine();
					// Pattern pattern = Pattern.compile("[0-9]*");
					if (message.equals("all")) {
						String str = scanner.nextLine();
						for (String key : socketMap.keySet()) {
							pw = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(socketMap.get(key)
											.getOutputStream())), true);
							pw.println(str);
							pw.flush();
						}

						continue;
					} else if (!socketMap.containsKey(message)) {
						System.out.println("请输入正确的socket代号！！！");
						continue;
					} else {

						pw = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socketMap.get(message)
										.getOutputStream())), true);

						pw.println(scanner.nextLine());
						pw.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void handlemessage(int port, String message) throws IOException {

		Map<String, Object> params = JsonUtil.parseJSON2Map(message);
		params.put("port", port);
		System.out.println(params);
		int stateCode = Integer.parseInt(params.get("state").toString());

		switch (stateCode) {
		case 0:
			register(params);
			break;

		case 1:
			login(params);
			break;

		default:
			sendMesagetoClient(params);
			break;
		}
	}

	private void login(Map<String, Object> params) {
		String email = params.get("email") == null ? "" : params.get("email")
				.toString();
		String key = params.get("key") == null ? "" : params.get("key")
				.toString();
		String appId = params.get("appId") == null ? "" : params.get("appId")
				.toString();
		String appKey = params.get("appKey") == null ? "" : params
				.get("appKey").toString();
		String port = params.get("port").toString();
		Connection connection = JDBCUtil();
		PreparedStatement statement;
		PrintWriter printWriter = null;
		try {
			String sql = "select email, password from  client_tb where appId= ? and appKey= ? ";
			statement = connection.prepareStatement(sql);
			statement.setString(1, appId);
			statement.setString(2, appKey);
			// statement.setString(3, appId);
			// statement.setString(4, appKey);
			ResultSet rs = statement.executeQuery();
			rs.last();
			System.out.println(rs);
			if (rs.getRow()!=0) {				
				rs.beforeFirst();
				String _email = "";
				String _key = "";				
				while (rs.next()) {
					_email = rs.getString("email");
					_key = rs.getString("password");
				}
				if (email.equals(_email) && key.equals(_key)) {
					printWriter = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socketMap.get(port)
									.getOutputStream())), true);
					String reply = "登陆成功！";
					printWriter.println(reply);
					printWriter.flush();
					socketMap.put(email, socketMap.get(port));
					// socketMap.remove(port);
					System.out.println(socketMap);
				
			}else {
				printWriter = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socketMap.get(port)
								.getOutputStream())), true);
				String reply = "登陆失败！邮箱或者密码错误！";
				printWriter.println(reply);
				printWriter.flush();
			}
			}else {				
				printWriter = new PrintWriter(new BufferedWriter(			
						new OutputStreamWriter(socketMap.get(port)
								.getOutputStream())), true);
				String reply = "用户不存在,请重新发送登陆信息";
				printWriter.println(reply);
				printWriter.flush();
				}
			connection.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void sendMesagetoClient(Map<String, Object> params) {
		String to_client = params.get("to").toString();
		String port = params.get("port").toString();
		String content = params.get("content").toString();
		try {
			if (!socketMap.containsKey(to_client)) {
				System.out.println("该用户未在线");
				PrintWriter printWriter = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socketMap.get(port)
								.getOutputStream())), true);
				printWriter.print("该用户未在线");
				printWriter.flush();
			}
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socketMap.get(to_client)
							.getOutputStream())), true);
			printWriter.print(content);
			printWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void register(Map<String, Object> params) {
		String email = params.get("email").toString();
		String key = params.get("key").toString();
		String appId = UUID.randomUUID().toString();
		String appKey = UUID.randomUUID().toString();
		String port = params.get("port").toString();
		Connection connection = JDBCUtil();
		PreparedStatement statement;
		PrintWriter printWriter = null;
		try {
			String sql = "insert into client_tb(email,password,appId,appKey) values(?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, email);
			statement.setString(2, key);
			statement.setString(3, appId);
			statement.setString(4, appKey);
			int rs = statement.executeUpdate();
			System.out.println(rs);
			if (rs > 0) {
				printWriter = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socketMap.get(port)
								.getOutputStream())), true);
				System.out.println(socketMap.get(port));
				params.clear();
				params.put("state", "register successfully");
				params.put("AppId", appId);
				params.put("AppKey", appKey);
				String jsonString = JsonUtil.getJson(params);
				System.out.println(jsonString);
				printWriter.println(jsonString);
				printWriter.flush();

			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Connection JDBCUtil() {

		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/zsjsql";
		String user = "root";
		String password = "admin";
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			if (!conn.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			/*
			 * Statement statement = conn.createStatement(); String sql =
			 * "select * from student"; ResultSet rs =
			 * statement.executeQuery(sql);
			 */

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
