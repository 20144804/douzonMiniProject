package server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lombok.Data;


@Data
public class RoomManager {
	List<ChatRoom> rooms;
	String LOGTXT= "log.txt";
	
	public RoomManager() {
		this.rooms = new Vector<>();
	}

	public void createChatRoom(String roomName, SocketClient socketClient) {
		ChatRoom chatRoom = new ChatRoom(rooms.size(), roomName, this);
		socketClient.chatRoom = chatRoom;
		rooms.add(chatRoom);
		chatRoom.entryRoom(socketClient);
		String path = ServerProperties.getWorkPath() + File.separatorChar + roomName;
		File chatRoomFileFolder = new File(path);
		System.out.println(chatRoomFileFolder.getAbsolutePath());
		if (!chatRoomFileFolder.exists()) {
			chatRoomFileFolder.mkdirs();
		}
		String logPath = path+File.separatorChar+LOGTXT;
		File logFile = new File(logPath); 
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void distroyChatRoom(ChatRoom chatRoom) {
		rooms.remove(chatRoom);
	}

	public List<String> getChatRoomList() {
		List<String> chatRoomList = new ArrayList<>();

		for (int i = 0; i < rooms.size(); i++) {
			chatRoomList.add(rooms.get(i).getRoomName());
		}

		return chatRoomList;
	}

	public int getChatRoomCount() {
		return rooms.size();
	}
}