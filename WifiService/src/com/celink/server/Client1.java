package com.celink.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client1 {

	/**
	 * Author: Lip 客户端
	 */
	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		Scanner scanner = new Scanner(System.in);// 从键盘读取
		try {
			// 创建客户端socket
			socket = new Socket(Server.HOST, Server.PORT);
			// 读取从客户端发来的消息
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// 写入信息到服务器端
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			new ReadServerMessage1(br, socket);// 从服务器读取消息
			// String message="";
			while (true) {
				// message=br.readLine();
				// System.out.println(message);
				String temp = scanner.nextLine();// 从键盘读取一行
				// pw.println(temp+"___come from client 1");// 写到服务器
				// pw.flush();
				if (temp.equals("register")) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("state", 0);
					params.put("email", "348555@qq.com");
					params.put("key", "123456");
					String jsonString = JsonUtil.getJson(params).toString();
					System.out.println(jsonString);
					pw.println(jsonString);
					pw.flush();
				} else if (temp.equals("login")) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("state", 1);
					params.put("email", "348555@qq.com");
					params.put("key", "123456");
					params.put("appId", "1625ba14-374b-484b-ac0a-a4e56820e1d9");
					params.put("appKey", "fbd9e0c0-7793-473d-b5a1-f68e217aee87");
					String jsonString = JsonUtil.getJson(params).toString();
					System.out.println(jsonString);
					pw.println(jsonString);
					pw.flush();
				} else {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("state", "100");
					params.put("to", "65162");
					params.put("content", "Oh i get you ");
					String jsonString = JsonUtil.getJson(params);
					char str[] = jsonString.toCharArray();
					pw.print(str);
					pw.flush();
				}
				if (temp.equals("q"))
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("close......");
				br.close();
				pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

class ReadServerMessage1 extends Thread// 从服务器读取消息
{
	BufferedReader bReader;
	Socket socket;

	public ReadServerMessage1(BufferedReader br, Socket s) {
		socket = s;
		this.bReader = br;
		start();
	}

	public void run() {
		String str = "";
		while (true)// 一直等待着服务器的消息
		{
			try {
				str = bReader.readLine();
				System.out.println(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(socket);
			if (socket != null && (!socket.isConnected())) {
				try {
					bReader.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
