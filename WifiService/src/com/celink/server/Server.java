package com.celink.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.celink.server.thread.ClientThread;
import com.celink.server.thread.ManagerClientThread;
import com.celink.util.PropertiesUtils;

public class Server {
	// 存储所有客户端线程
	private Map<String,Object> map = new HashMap<String, Object>();
	
	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	private static Server server = new Server();

	private Server() {

	}

	public static Server getInstance() {
		if (server == null) {
			server = new Server();
		}
		// 心跳检测
		new ManagerClientThread(server).start();
		return server;
	}
	
	public static String getConstantValue(String key) {
		return PropertiesUtils.getInstance("config").getValue(key);
	}

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(Integer.parseInt(getConstantValue("port")));
			
			while (true) {
				System.out.println("Waiting Client");
				socket = serverSocket.accept();//有客户端连接
				System.out.println("Client Conect!");
				new ClientThread(socket, getInstance()).start();
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
}
