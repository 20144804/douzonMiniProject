package main;

import client.Menu;

public class ClientMain {
	public static void main(String[] args) {
		try {
			new Menu().mainMenu();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("[클라이언트] 서버 연결 안됨");
		}
	}
}