package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import member.Member;
import server.ChatServer;
import server.RoomManager;

public class ChatClient {
	// 필드
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String roomName;
	Menu menu;

	static Member member = null;
	int roomNum;
	static List<String> chatRooms = new ArrayList<String>();

	// 메소드: 서버 연결
	public void connect() throws IOException {
		socket = new Socket("localhost", ClientProperties.getPort());
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		menu = new Menu();
		System.out.println("[클라이언트] 서버에 연결됨sss");
	}

	// 메소드: JSON 받기
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while (true) {
					String json = serverDataRead();
					System.out.println(json);
					
					JSONObject root = new JSONObject(json);
					String uid = root.getString("uid");
					String message = root.getString("message");
					System.out.println("<" + uid + "> " + message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[클라이언트] 서버 연결 끊김");
				System.exit(0);
			}
		});
		thread.start();
	}

	public void login(String uid, String pwd) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "login");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			send(jsonObject.toString());

			loginResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void loginResponse() throws Exception {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			member = new Member(root.getJSONObject("member"));
			System.out.println("loginMember = " + member);
			System.out.println("로그인 성공");
			menu.mm = MenuMode.LOGIN_MENU;
		} else {
			System.out.println(message);
		}
	}

	public void registerMember(Member member) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "registerMember");
			jsonObject.put("uid", member.getUid());
			jsonObject.put("pwd", member.getPwd());
			jsonObject.put("name", member.getName());
			jsonObject.put("sex", member.getSex());
			jsonObject.put("address", member.getAddress());
			jsonObject.put("phone", member.getPhone());
			send(jsonObject.toString());

			registerMemberResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerMemberResponse() throws Exception {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("회원가입성공");
		} else {
			System.out.println(message);
		}
	}

	public void passwdSearch(String uid) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "passwdSearch");
			jsonObject.put("uid", uid);
			String json = jsonObject.toString();
			send(json);

			passwdSearchResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void passwdSearchResponse() throws Exception {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("비밀번호 : " + root.getString("pwd"));
		} else {
			System.out.println(message);
		}
	}

	public void logout() throws IOException {
		member = null;
		disconnect();
	}

	public void updateMember(Member member) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "updateMember");
			jsonObject.put("uid", member.getUid());
			jsonObject.put("pwd", member.getPwd());
			jsonObject.put("name", member.getName());
			jsonObject.put("sex", member.getSex());
			jsonObject.put("address", member.getAddress());
			jsonObject.put("phone", member.getPhone());
			send(jsonObject.toString());

			updateMemberResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateMemberResponse() throws Exception {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("정상적으로 수정되었습니다");
		} else {
			System.out.println(message);
		}
	}

	// 여기 손보기
	public void deleteMember(String pwd) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "deleteMember");
			jsonObject.put("uid", member.getUid());
			jsonObject.put("pwd", pwd);
			send(jsonObject.toString());

			deleteMemberResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteMemberResponse() throws Exception {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("정상적으로 삭제되었습니다");
			logout();
		} else {
			System.out.println(message);
		}
	}

	// 손보기
	public void createChatRoom(Scanner sc) {
		try {
			System.out.println("\n1. 채팅방 생성");
			System.out.print("채팅방 이름 : ");
			roomName = sc.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "createChatRoom");
			jsonObject.put("uid", member.getUid());
			jsonObject.put("roomName", roomName);
			send(jsonObject.toString());

			createChatRoomResponse();
			enterRoom(sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createChatRoomResponse() throws IOException {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	// 손보기
	public void chatRoomListRequest(Scanner scanner) {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "chatRoomListRequest");
			send(jsonObject.toString());

			chatRoomListResponse();
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 손보기
	private void chatRoomListResponse() throws IOException {
		String json = serverDataRead();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			chatRooms.clear();
			root.getJSONArray("chatRooms").forEach(s -> chatRooms.add((String) s));
			displayChattingRoomList();
		} else {
			System.out.println(message);
		}
	}

	private int getChatRoomsCount() {
		return chatRooms.size();
	}

	// 손보기
	private void displayChattingRoomList() {
		int idx = 1;

		if (0 == getChatRoomsCount()) {
			System.out.println("* 입장 가능한 채팅방이 없습니다. 채팅방 생성을 먼저 생성하세요 *");
			return;
		}

		System.out.println("----------------");
		System.out.println("* 채팅방 목록 *");
		for (String chatRoom : chatRooms) {
			System.out.println(idx + ". " + chatRoom);
			idx++;
		}
	}

	// 손보기
	public void enterRoomRequest(Scanner scanner) throws IOException {
		chatRoomListRequest(scanner);

		if (getChatRoomsCount() == 0) {
			return;
		}

		System.out.print("입장할 채팅방 번호 : ");
		int roomNum = Integer.parseInt(scanner.nextLine());

		if (0 >= roomNum || roomNum > getChatRoomsCount()) {
			System.out.print("채팅방 번호를 잘못 입력하셨습니다");
			return;
		}

		roomName = chatRooms.get(roomNum-1);
		this.roomNum = roomNum-1;
		
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("command", "enterRoomRequest");
		jsonObject.put("uid", member.getUid());
		jsonObject.put("roomNum", this.roomNum);
		jsonObject.put("roomName", roomName);
		send(jsonObject.toString());

		enterRoom(scanner);
	}

	// 손보기
	public void enterRoom(Scanner scanner) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", "incoming");
			jsonObject.put("uid", member.getUid());
			jsonObject.put("roomNum", roomNum);
			jsonObject.put("roomName", roomName);
			send(jsonObject.toString());

			enterRoomResponse();
			inputChatMessage(scanner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enterRoomResponse() throws IOException {
		// 메시지 수신을 위한 스레드 구동
		receive();
	}

	// 손보기
	private void inputChatMessage(Scanner scanner) throws IOException {
		System.out.println("--------------------------------------------------");
		System.out.println("보낼 메시지를 입력하고 Enter");
		System.out.println("채팅를 종료하려면 q를 입력하고 Enter");
		System.out.println(
				"채팅방에 참여자 목록(@userlist), 귀속말(@아이디), 첨부파일 업로드(@up:파일명), 첨부파일목록 요청(@filelist), 첨부파일 다운로드(@download:파일명), 첨부파일 업로드 또는 다운로드중 취소 (@cancel)");
		System.out.println("--------------------------------------------------");
		JSONObject jsonObject = new JSONObject();
		while (true) {
			String message = scanner.nextLine();

			if (message.toLowerCase().equals("q")) {
				
				break;
			} else if (message.startsWith("@up:")) {
				String fileName = message.substring("@up:".length());
				File file = new File(fileName);
				if (!file.exists()) {
					System.out.println("업로드할 파일이 존재하지 않습니다");
				} else {

					new Thread(() -> {
						try {
							// 전송할 파일의 내용을 읽는다
							BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
							byte[] data = new byte[(int) file.length()];
							in.read(data);
							in.close();

							// 전송할 메시지를 구성한다
							jsonObject.put("command", "fileUpload");
							jsonObject.put("roomName", roomName);
							jsonObject.put("fileName", file.getName());
							jsonObject.put("content", new String(Base64.getEncoder().encode(data)));

							String json = jsonObject.toString();
							// 서버에 연결
							Socket socket = new Socket("localhost", ClientProperties.getPort());
							DataInputStream dis = new DataInputStream(socket.getInputStream());
							DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

							byte[] sendData = json.getBytes("UTF8");
							// 서버에 첨부파일 전송
							dos.writeInt(sendData.length);// 문자열의 길이(4byte)
							dos.write(sendData);// 내용
							dos.flush();

							// 서버에서 결과 수신
							int length = dis.readInt();
							int pos = 0;
							byte[] recvData = new byte[length];
							do {
								int len = dis.read(recvData, pos, length - pos);
								pos += len;
							} while (length != pos);

							String responseJson = new String(recvData, "UTF8");
							JSONObject root = new JSONObject(responseJson);
							String statusCode = root.getString("statusCode");

							// 서버의 처리 결과를 출력한다
							System.out.println(root.getString("message"));

							// 서버와 연결을 끊는다
							socket.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}).start();
				}
			} else if (message.startsWith("@download:")) {
				String fileName = message.substring("@download:".length());

				new Thread(() -> {
					try {
						// 전송할 메시지를 구성한다
						jsonObject.put("command", "download");
						jsonObject.put("roomName", roomName);
						jsonObject.put("fileName", fileName);

						String json = jsonObject.toString();
						// 서버에 연결
						Socket socket = new Socket("localhost", ClientProperties.getPort());
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

						byte[] sendData = json.getBytes("UTF8");
						// 서버에 첨부파일 전송
						dos.writeInt(sendData.length);// 문자열의 길이(4byte)
						dos.write(sendData);// 내용
						dos.flush();

						// 서버에서 결과 수신
						int length = dis.readInt();
						int pos = 0;
						byte[] recvData = new byte[length];
						do {
							int len = dis.read(recvData, pos, length - pos);
							pos += len;
						} while (length != pos);

						String responseJson = new String(recvData, "UTF8");
						JSONObject root = new JSONObject(responseJson);
						String statusCode = root.getString("statusCode");

						// 서버와 연결을 끊는다
						socket.close();

						if ("0".equals(statusCode)) {
							// 서버에 받은 정보를 기준으로 파일 저장을 한다
							byte[] data = Base64.getDecoder().decode(root.getString("content").getBytes());

							System.out.println("WorkPath : " + ClientProperties.getFilePath());
							File workPath = new File(ClientProperties.getFilePath());
							if (!workPath.exists()) {
								workPath.mkdirs();
							}

							File file = new File(workPath, fileName);
							try {
								System.out.println("저장위치 : " + file.getAbsolutePath());
								BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
								fos.write(data);
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println("파일 다운로드 완료");
						} else {
							System.out.println(root.getString("message"));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			} else {
				jsonObject.put("command", "message");
				jsonObject.put("roomName", roomName);
				jsonObject.put("data", message);
				send(jsonObject.toString());
			}
		}
		socket.close();
	}

	// 메소드: JSON 보내기
	public void send(String json) throws IOException {
		byte[] data = json.getBytes("UTF8");

		dos.writeInt(data.length);
		dos.write(data);// 내용
		dos.flush();
	}

	private String serverDataRead() throws IOException {
		int length = dis.readInt();
		int pos = 0;
		byte[] data = new byte[length];
		do {
			int len = dis.read(data, pos, length - pos);
			pos += len;
		} while (length != pos);

		return new String(data, "UTF8");
	}

	// 메소드: 서버 연결 종료
	public void disconnect() throws IOException {
		socket.close();
	}

	// 메소드: 메인
//	public static void main(String[] args) {
//		try {
//			ChatClient chatClient = new ChatClient();
//			boolean stop = false;
//
//			while (false == stop) {
//				System.out.println();
//				System.out.println("1. 로그인");
//				System.out.println("2. 회원가입");
//				System.out.println("3. 비밀번호검색(정상)");
//				System.out.println("4. 비밀번호검색(비정상)");
//				System.out.println("q. 프로그램 종료");
//				System.out.print("메뉴 선택 => ");
//				Scanner scanner = new Scanner(System.in);
//				String menuNum = scanner.nextLine();
//				switch (menuNum) {
//				case "1":
////						chatClient.login(scanner);
//					break;
//				case "2":
//					chatClient.registerMember(scanner);
//					break;
//				case "3":
//					chatClient.passwdSearch(scanner, "userid");
//					break;
//				case "4":
//					chatClient.passwdSearch(scanner, "userid1");
//					break;
//				case "Q", "q":
//					scanner.close();
//					stop = true;
//					System.out.println("프로그램 종료됨");
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("[클라이언트] 서버 연결 안됨");
//		}
//	}
}