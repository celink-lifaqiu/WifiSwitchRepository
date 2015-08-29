package com.celink.server.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.celink.server.Server;

public class ManagerClientThread extends Thread {

	private Server server;

	public ManagerClientThread(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		while (true) {
			// 6分钟检测一次所有socket，如果失去连接的，删除
			try {
				Thread.sleep(6 * 60 * 1000);
				List<String> list = new ArrayList<String>();
				for (String key : this.server.getMap().keySet()) {
					ClientThread ct = (ClientThread) this.server.getMap().get(key);
					try {
						ct.getSocket().sendUrgentData(0xFF);
					} catch (IOException e) {
						list.add(key);
					}
				}
				for(String key: list){
					this.server.getMap().remove(key);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
