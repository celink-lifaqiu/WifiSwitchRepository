package com.celink.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ThreadedServer extends Thread {
	Map<String, Socket> socktmap;
	private Socket socket = null;
	private BufferedReader br = null;	
	Server server=Server.getInstance(); 
	public ThreadedServer(Socket s,Map<String, Socket>map) {
		socket = s;
		socktmap=map;
		try {
			
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));		
			start();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void run() {
		String str = "";
		while (true) {   //接收数据
			try {
				str = br.readLine();
				System.out.println(socket.getPort()+":" + str);
				server.handlemessage(socket.getPort(),str);
				if (str.equals("q")) {
					br.close();
					socket.close();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	
	}

}

