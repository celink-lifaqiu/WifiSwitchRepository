package com.celink.server.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.celink.Constants.Constants;
import com.celink.action.pojo.User;
import com.celink.server.Server;
import com.celink.service.UserService;
import com.celink.service.impl.UserServiceImpl;
import com.celink.util.JsonUtil;
import com.celink.util.PropertiesUtils;

public class ClientThread extends Thread{
	public static final String uuid = UUID.randomUUID().toString();
	
	private UserService userService;
	private Socket socket;
	private BufferedReader br;	
	private PrintWriter pw;
	public boolean isStop = false;
	private Server server = null;
	public ClientThread(Socket socket,Server server){
		this.socket = socket;
		this.server = server;
		init();
	}
	
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private void init(){
		this.userService = new UserServiceImpl();
		if(socket != null){
			try{
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));	
				pw = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
			}catch (IOException e) {
				System.out.println(ClientThread.class + "====67======" + e.getMessage());
				try {
					socket.close();
				} catch (IOException e1) {
					System.out.println(ClientThread.class + "====71======" + e1.getMessage());
				}
			}
			
		}
	}
	
	public void close(){
		isStop = true;
		try {
			if(this.br != null){
				br.close();
			}
			if(this.pw != null){
				pw.close();
			}
			if(this.socket != null){
				socket.close();
			}
		} catch (IOException e) {

		}
	}
	
	@Override
	public void run() {
		String receiveMsg = "";
		while (!isStop) {   //接收数据
			try {
				// 接受的数据格式：{"type":int,"jsonData":{}}
				receiveMsg = br.readLine();
				Map<String, Object> map = JsonUtil.parseJSON2Map1(receiveMsg);
				
				switch (Integer.parseInt(map.get("type").toString())) {
					case Constants.REGISTER:
						int code = this.userService.regist(map.get("jsonData").toString());
						if(code != 0){
							sendToClientMsg(1, getConstantValue(code), null);
							// 关闭连接
							close();
						}else{
							JSONObject jb = new JSONObject();
							jb.put("state", true);
							sendToClientMsg(0, "", jb);
						}
						break;
					case Constants.LOGIN:
						Object ob = this.userService.login(map.get("jsonData").toString());
						if(ob instanceof Integer){
							sendToClientMsg(Integer.parseInt(ob.toString()), getConstantValue(Integer.parseInt(ob.toString())), null);
							// 关闭连接
							close();
						}else{
							sendToClientMsg(0, "", ob);
							// 把此线程加入Server管理
							joinToServerSockets();
						}
						break;
					case Constants.LOGINOUT:
						// 关闭连接
						close();
						this.getServer().getMap().remove(uuid);
						break;
					default:
						sendToClientMsg(1, getConstantValue(3), null);
						break;
					}
				
			}catch (JSONException e) {
				sendToClientMsg(1, getConstantValue(2), null);
			}catch (IOException e) {
				// 关闭连接
				close();
				this.getServer().getMap().remove(uuid);
			}
		}
	}
	
	public void joinToServerSockets(){
		this.server.getMap().put(uuid, this);
	}
	
	public void sendToClientMsg(int code,String err, Object result){
		JSONObject jb = new JSONObject();
		jb.put("code", code);
		jb.put("err", err);
		jb.put("result", result);
		if(pw != null){
			try {
				pw.println(JsonUtil.getJson(jb));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// 关闭连接
				close();
				this.getServer().getMap().remove(uuid);
			}
		}
	}
	
	public String getConstantValue(int code) {
		return PropertiesUtils.getInstance("constants").getValue(code+"");
	}
}
