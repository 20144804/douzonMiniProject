package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import member.Member;
import member.MemberRepository;

public class ChatServer {
	// 필드
	ServerSocket serverSocket;
	ExecutorService threadPool;
	MemberRepository memberRepository;
	Map<String, SocketClient> AllMember;
	RoomManager rm;
	ChatRoom chatRoom;

	public ChatServer() {
		threadPool = Executors.newFixedThreadPool(ServerProperties.getThreadPoolSize());
		memberRepository = new MemberRepository();
		AllMember = Collections.synchronizedMap(new HashMap<>());
		rm = new RoomManager();

		try {
			serverSocket = new ServerSocket(ServerProperties.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 메소드: 서버 시작
	public void start() throws IOException {
		memberRepository.loadMember();
		System.out.println("[서버] 시작됨");

		Thread thread = new Thread(() -> {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					SocketClient sc = new SocketClient(this, socket, rm);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public void addSocketClient(SocketClient socketClient) {
		String key = socketClient.chatName + "@" + socketClient.clientIp;
		AllMember.put(key, socketClient);
		System.out.println("입장 : " + key);
		System.out.println("현재 채팅방 맴버 수 : " + rm.rooms.size() + "\n");
	}

	// 손보기
	// 메소드: 클라이언트 연결 종료시 SocketClient 제거
//	public void removeSocketClient(SocketClient socketClient) {
//		String key = socketClient.chatName + "@" + socketClient.clientIp;
//		AllMember.remove(key);
//		System.out.println("나감: " + key);
//		System.out.println("현재 접속자 수: " + AllMember.size() + "\n");
//	}

	// 메소드: 모든 클라이언트에게 메시지 보냄
	public void sendMessage(SocketClient socketClient, String message) throws Exception {
		JSONObject root = new JSONObject();
		root.put("uid", socketClient.uid);
//		root.put("chatName", socketClient.chatName);
//		root.put("roomName", socketClient.roomName);
		root.put("message", message);
		String json = root.toString();
		System.out.println(json);
		socketClient.sendChatRoomMessage(json);
	}

	// 메소드: 서버 종료
	public void stop() {
		try {
			serverSocket.close();
			threadPool.shutdownNow();
			AllMember.values().stream().forEach(sc -> sc.close());
			System.out.println("[서버] 종료됨 ");
		} catch (IOException e1) {
		}
	}
	
	// 메소드: 메인
//	public static void main(String[] args) {
//		try {
//			ChatServer chatServer = new ChatServer();
//			chatServer.start();
//
//			System.out.println("----------------------------------------------------");
//			System.out.println("서버를 종료하려면 q를 입력하고 Enter.");
//			System.out.println("----------------------------------------------------");
//
//			Scanner scanner = new Scanner(System.in);
//			while (true) {
//				String key = scanner.nextLine();
//				if (key.equals("q"))
//					break;
//			}
//			scanner.close();
//			chatServer.stop();
//		} catch (IOException e) {
//			System.out.println("[서버] " + e.getMessage());
//		}
//	}
}