package member;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MemberRepositoryDB {
//	List<Member> memberList = null;
//	Map<String, Member> memberMap = null;
	Connection conn;
	
	@SuppressWarnings("unchecked")
	public void loadMember() {
//		try {
//			File file = new File(ServerProperties.getMemberFileName());
//			if (file.exists() && file.length() != 0) {
//				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
//				memberList = (List<Member>) in.readObject();
//				memberMap = memberList.stream()
//						.collect(Collectors.toMap(
//						m -> m.getUid(),
//						m -> m));
//				in.close();
//			} else {
//				memberList = new ArrayList<>();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void saveMember() {
//		try {
//			File file = new File(ServerProperties.getMemberFileName());
//			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
//			out.writeObject(memberList);
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public synchronized void insertMember(Member member) {
		// 1. DB에 연결한다.
		// 2. uid가 DB에 존재하는지 확인한다
		//    select count(*) from member where userid = 'uid'
		//    count 값이 0 이면 존재하지 않음
		//    count 값이 0 이상 이면 존재함
		// 3. uid가 DB에 존재 하지 않으면 추가한다
		//    insert into 
		// 4. DB 연결종료 한다.
		
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/memeber", "root", "passwd");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void updateMember(Member member) {
		
	}
	
	public synchronized void deleteMember(String uid, String pwd) {
		
	}
}