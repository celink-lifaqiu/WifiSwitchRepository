package com.celink.client;

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

import com.celink.server.Server;
import com.celink.util.JsonUtil;

public class Client {

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
			socket = new Socket("192.168.4.183", 8888);
			// 读取从客户端发来的消息
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// 写入信息到服务器端
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			new ReadServerMessage(br, socket);// 从服务器读取消息
			// String message="";
			while (true) {
				// message=br.readLine();
				// System.out.println(message);
				String temp = scanner.nextLine();// 从键盘读取一行
				pw.println(temp);
				pw.flush();
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

class ReadServerMessage extends Thread// 从服务器读取消息
{
	BufferedReader bReader;
	Socket socket;

	public ReadServerMessage(BufferedReader br, Socket s) {
		socket = s;
		this.bReader = br;
		start();
	}

	public void run() {
		String str = "";
		while (true)// 一直等待着服务器的消息
		{
			try {
				if(bReader != null){
					str = bReader.readLine();
					System.out.println(str);
				}else{
					interrupt();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
