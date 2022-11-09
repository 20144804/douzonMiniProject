package server;

import java.util.List;
import java.util.Vector;

import lombok.Data;

@Data
public class ChatRoom {
	private int roomNum;
	private String roomName;
	public RoomManager roomManager;
	public List<SocketClient> roomMembers;
	
	public ChatRoom(int roomNum, String roomName, RoomManager roomManager) {
		this.roomNum = roomNum;
		this.roomName = roomName;
		this.roomManager = roomManager;
		this.roomMembers = new Vector<>();
	}
	
	public void entryRoom(SocketClient sc) {
		roomMembers.add(sc);
		sc.chatRoom = this;
	}
	
	public void leaveRoom(SocketClient sc) {
        this.roomMembers.remove(sc);
        sc.chatRoom = null;
        if(this.roomMembers.size() < 1) {
            roomManager.distroyChatRoom(this);
        }
    }
	
	public List<SocketClient> getRoomMemberList() {
		return roomMembers;
	}
}