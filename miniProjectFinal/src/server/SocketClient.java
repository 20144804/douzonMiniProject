package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import client.Menu;
import member.Member;
import member.MemberRepository;

public class SocketClient {
	// 필드
	ChatServer chatServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String clientIp;
	String chatName;
	String roomName;
	RoomManager rm;
	ChatRoom chatRoom;
	String uid;

	// 생성자
	public SocketClient(ChatServer chatServer, Socket socket, RoomManager rm) {
		try {
			this.chatServer = chatServer;
			this.socket = socket;
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
			this.clientIp = isa.getHostName();
			this.rm = rm;

			receive();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String clientDataRead() throws IOException {
		int length = dis.readInt();
		int pos = 0;
		byte[] data = new byte[length];
		do {
			int len = dis.read(data, pos, length - pos);
			pos += len;
		} while (length != pos);

		return new String(data, "UTF8");
	}

	// 메소드: JSON 받기
	public void receive() {
		chatServer.threadPool.execute(() -> {
			try {
				boolean stop = false;

				while (true != stop) {
					String receiveJson = clientDataRead();

					JSONObject jsonObject = new JSONObject(receiveJson);
					String command = jsonObject.getString("command");
					System.out.println(jsonObject.toString());

					switch (command) {
					case "login":
						login(jsonObject);
						stop = true;
						break;
					case "registerMember":
						registerMember(jsonObject);
						stop = true;
						break;
					case "passwdSearch":
						passwdSearch(jsonObject);
						stop = true;
						break;
					case "updateMember":
						updateMember(jsonObject);
						stop = true;
						break;
					case "deleteMember":
						deleteMember(jsonObject);
						stop = true;
						break;
					case "createChatRoom":
						createChatRoom(jsonObject);
						break;
					case "chatRoomListRequest":
						chatRoomListRequest(jsonObject);
						stop = true;
						break;
					case "enterRoomRequest":
						enterRoomRequest(jsonObject);
						break;
					case "message":
						chatServer.sendMessage(this, jsonObject.getString("data"));
						break;
					case "incoming":
						this.uid = jsonObject.getString("uid");
						this.roomName = jsonObject.getString("roomName");
						chatServer.sendMessage(this, "들어오셨습니다.");
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					chatServer.sendMessage(this, "나가셨습니다.");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
//				chatServer.removeSocketClient(this);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void sendChatRoomMessage(String message) {
		int index = 0;

//		JSONObject root = new JSONObject();
//		root.put("roomName", roomName);

		for (int i = 0; i < rm.getChatRoomCount(); i++) {
			if (rm.rooms.get(i).getRoomName().equals(roomName)) {
				index = i;
			}
		}

		for (SocketClient c : rm.rooms.get(index).getRoomMembers()) {
			System.out.println(c.toString());
			if (!c.equals(this)) {
				String json = message;
				c.send(json);
			}
		}
	}

	private void login(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		String pwd = jsonObject.getString("pwd");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		Member member = chatServer.memberRepository.findByUid(uid);
		if (null != member && pwd.equals(member.getPwd())) {
			member.setLoginDateTime(LocalDateTime.now());
			jsonResult.put("statusCode", "0");
			jsonResult.put("member", member.getJsonObject());
			jsonResult.put("message", "로그인 성공");
		}

		send(jsonResult.toString());
		close();
	}

	private void registerMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "아이디가 존재합니다");

		try {
			chatServer.memberRepository.insertMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원가입이 정상처리 되었습니다");
		} catch (Exception e) { // ExistMember
			e.printStackTrace();
			jsonResult.put("message", e.getMessage());
		}

		send(jsonResult.toString());
		close();
	}

	private void passwdSearch(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");
		Member member = chatServer.memberRepository.findByUid(uid);
		if (null != member) {
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "비밀번호 찾기 성공");
			jsonResult.put("pwd", member.getPwd());
		}

		send(jsonResult.toString());
		close();
	}

	private void updateMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "문제가 있습니다");

		try {
			chatServer.memberRepository.updateMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원수정이 정상처리 되었습니다");
		} catch (Exception e) { // ExistMember
			e.printStackTrace();
			jsonResult.put("message", e.getMessage());
		}

		send(jsonResult.toString());
		close();
	}

	private void deleteMember(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		String pwd = jsonObject.getString("pwd");
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "회원정보가 일치하지 않습니다.");

		try {
			chatServer.memberRepository.deleteMember(uid, pwd);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원탈퇴가 정상처리 되었습니다");
		} catch (Exception e) { // ExistMember
			e.printStackTrace();
			jsonResult.put("message", e.getMessage());
		}

		send(jsonResult.toString());
		close();
	}

	// 손보기
	private void createChatRoom(JSONObject jsonObject) {
		JSONObject jsonResult = new JSONObject();
		uid = jsonObject.getString("uid");
		roomName = jsonObject.getString("roomName");

		try {
			rm.createChatRoom(roomName, this);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "[" + roomName + "] 채팅방이 생성되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.put("statusCode", "-1");
			jsonResult.put("message", "[" + roomName + "] 채팅방은 이미 존재합니다");
		}

		send(jsonResult.toString());
	}

	// 손보기
	private void chatRoomListRequest(JSONObject jsonObject) {
		List<String> chatRoomList = rm.getChatRoomList();
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "0");
		jsonResult.put("message", "채팅방 목록 조회");
		jsonResult.put("chatRooms", chatRoomList);

		send(jsonResult.toString());
		close();
	}

	// 손보기
	//추가 roomName
	private void enterRoomRequest(JSONObject jsonObject) {
		roomName = jsonObject.getString("roomName");
		int roomNum = jsonObject.getInt("roomNum");
		uid = jsonObject.getString("uid");
		JSONObject jsonResult = new JSONObject();

		try {
			rm.rooms.get(roomNum).entryRoom(this);
			jsonResult.put("uid", uid);
			jsonResult.put("message", "채팅방 입장");
		} catch (Exception e) {
			e.printStackTrace();
		}

		send(jsonResult.toString());
	}

	public String getRoomName() {
		return roomName;
	}

	// 메소드: JSON 보내기
	public void send(String json) {
		try {
			byte[] data = json.getBytes("UTF8");

			dos.writeInt(data.length);
			dos.write(data);// 내용
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 메소드: 연결 종료
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}