package member;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import server.ServerProperties;

public class MemberRepository {
	List<Member> memberList = null;
	Map<String, Member> memberMap = null;
	
	@SuppressWarnings("unchecked")
	public void loadMember() {
		try {
			File file = new File(ServerProperties.getMemberFileName());
			if (file.exists() && file.length() != 0) {
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
				memberList = (List<Member>) in.readObject();
				memberMap = memberList.stream()
						.collect(Collectors.toMap(
						m -> m.getUid(),
						m -> m));
				in.close();
			} else {
				memberList = new ArrayList<>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveMember() {
		try {
			File file = new File(ServerProperties.getMemberFileName());
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			out.writeObject(memberList);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized Member findByUid(String uid) {
		Member findMember = memberMap.get(uid);
		
		if (null == findMember) {
			System.out.println("[" + uid + "] 아이디가 존재하지 않습니다.");
			return null;
		}
		
		return findMember;
	}
	
	public synchronized Member overlabUid(String uid) {
		Member findMember = memberMap.get(uid);
		
		if (null == findMember) {
			System.out.println("[" + uid + "] 사용가능한 아이디입니다.");
			return findMember;
			
		} else {
			System.out.println("[" + uid + "] 아이디가 존재합니다.");
			return null;
		}
	}
	
	public synchronized void insertMember(Member member) {
		if (true == memberList.stream().anyMatch(m -> member.getUid().equals(m.getUid()))) {
			overlabUid(member.getUid());
		}
		memberList.add(member);
		memberMap.put(member.getUid(), member);
		saveMember();
	}
	
	public synchronized void updateMember(Member member) {
		memberList.removeIf((m) -> m.getUid().equals(member.getUid()));
		memberList.add(member);
		memberMap.put(member.getUid(), member);
		saveMember();
		System.out.println(member.getUid() + "님의 정보 수정 성공");
	}
	
	public synchronized void deleteMember(String uid, String pwd) {
		Member findMember = findByUid(uid);
		
		if (findMember.getPwd().equals(pwd)) {
			int index = memberList.indexOf(findMember);
			memberList.remove(index);
			memberMap.remove(uid);
			saveMember();
		}
	}
}