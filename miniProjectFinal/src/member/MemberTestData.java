package member;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import server.ServerProperties;

public class MemberTestData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			List<Member> memberList = new ArrayList<>();
			
			for(int i=0;i<10;i++) {
				memberList.add(Member.builder()
						.uid(String.valueOf(i))
						.pwd(String.valueOf(i))
						.name("홍길동" + i)
						.sex(i % 2 == 0 ? "F" : "M")
						.phone("010-1234-123" + i)
						.address("혜화동" + i)
						.loginDateTime(null)
						.build());
			}
			File file = new File(ServerProperties.getMemberFileName());
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			out.writeObject(memberList);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}