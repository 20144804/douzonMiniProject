package client;

import java.io.IOException;
import java.util.Scanner;

import member.Member;
import member.MemberRepository;
import server.RoomManager;

public class Menu {
	static MenuMode mm;
	MemberRepository mr;
	ChatClient chatClient;

	public Menu() {
		mm = MenuMode.NOT_LOGIN_MENU;
		mr = new MemberRepository();
		chatClient = new ChatClient();
	}

	public void mainMenu() {
		boolean stop = false;

		while (false == stop) {
			menuDisplay();

			System.out.print("메뉴 선택 => ");
			Scanner scanner = new Scanner(System.in);
			String menuNum = scanner.nextLine();

			switch (mm) { // 초기 메뉴
			case NOT_LOGIN_MENU:
				switch (menuNum) {
				case "1":
					loginMenu(scanner);
					break;
				case "2":
					registerMemberMenu(scanner);
					break;
				case "3":
					passwdSearchMenu(scanner);
					break;
				case "Q", "q":
					scanner.close();
					stop = true;
					System.out.println("프로그램 종료됨");
					break;
				}
				break;

			case LOGIN_MENU: // 로그인후 메뉴
				switch (menuNum) {
				case "1":
					logoutMenu();
					break;
				case "2":
					updateMemberMenu(scanner);
					break;
				case "3":
					chatMenu();
					break;
				case "4":
					deleteMemberMenu(scanner);
					break;
				case "Q", "q":
					scanner.close();
					stop = true;
					System.out.println("프로그램 종료됨");
					break;
				}
				break;

			case CHAT_MANAGER_MENU:
				switch (menuNum) {
				case "1":
					createChatRoomMenu(scanner);
					break;
				case "2":
					chatRoomListRequestMenu(scanner);
					mm = MenuMode.CHAT_MANAGER_MENU;
					break;
				case "3":
					enterChatRoomMenu(scanner);
					mm = MenuMode.CHAT_MANAGER_MENU;
					break;
				case "Q", "q":
					mm = MenuMode.LOGIN_MENU;
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	private void menuDisplay() {
		System.out.println();
		switch (mm) {
		case NOT_LOGIN_MENU:
			System.out.println("1. 로그인");
			System.out.println("2. 회원가입");
			System.out.println("3. 비밀번호검색");
			System.out.println("q. 프로그램 종료");
			break;
		case LOGIN_MENU:
			System.out.println("1. 로그아웃");
			System.out.println("2. 회원정보수정");
			System.out.println("3. 채팅");
			System.out.println("4. 회원탈퇴");
			System.out.println("q. 종료");
			break;
		case CHAT_MANAGER_MENU:
			System.out.println("1. 채팅방 생성");
			System.out.println("2. 채팅방 목록");
			System.out.println("3. 채팅방 참여");
			System.out.println("q. 이전 메뉴");
			break;
//        case CHAT_MENU:
//            displayChattingRoomList();
//            System.out.println("q. 이전 메뉴");
		default:
			break;
		}
	}

	private void loginMenu(Scanner sc) {
		String id;
		String pw;

		System.out.println();
		System.out.print("ID : ");
		id = sc.nextLine();
		System.out.print("PW : ");
		pw = sc.nextLine();

		chatClient.login(id, pw);
	}

	private void registerMemberMenu(Scanner sc) {
		String uid;
		String pwd;
		String name;
		String sex;
		String address;
		String phone;

		System.out.println("\n2. 회원가입");
		System.out.print("아이디 : ");
		uid = sc.nextLine();
		System.out.print("비밀번호 : ");
		pwd = sc.nextLine();
		System.out.print("이름 : ");
		name = sc.nextLine();
		System.out.print("성별(M/F) : ");
		sex = sc.nextLine();
		System.out.print("주소 : ");
		address = sc.nextLine();
		System.out.print("전화번호 : ");
		phone = sc.nextLine();

		Member member = new Member(uid, pwd, name, sex, address, phone);
		chatClient.registerMember(member);
	}

	private void passwdSearchMenu(Scanner sc) {
		String uid;

		System.out.println("\n3. 비밀번호 검색");
		System.out.print("아이디 : ");
		uid = sc.nextLine();

		chatClient.passwdSearch(uid);
	}

	private void logoutMenu() {
		mm = MenuMode.NOT_LOGIN_MENU;
		try {
			chatClient.logout();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateMemberMenu(Scanner sc) {
		String uid;
		String pwd;
		String name;
		String sex;
		String address;
		String phone;

		System.out.println("\n2. 회원정보수정");
		System.out.print("아이디 : ");
		uid = sc.nextLine();
		System.out.print("비번 : ");
		pwd = sc.nextLine();
		System.out.print("이름 : ");
		name = sc.nextLine();
		System.out.print("성별[남자(M)/여자(F)] : ");
		sex = sc.nextLine();
		System.out.print("주소 : ");
		address = sc.nextLine();
		System.out.print("전화번호 : ");
		phone = sc.nextLine();

		Member member = new Member(uid, pwd, name, sex, address, phone);
		chatClient.updateMember(member);
	}

	private void chatMenu() {
		mm = MenuMode.CHAT_MANAGER_MENU;
	}

	private void deleteMemberMenu(Scanner sc) {
		String pwd;

		System.out.println("\n4. 회원탈퇴");
		System.out.print("비번 : ");
		pwd = sc.nextLine();

		chatClient.deleteMember(pwd);
	}

	private void createChatRoomMenu(Scanner sc) {
		chatClient.createChatRoom(sc);
	}

	private void chatRoomListRequestMenu(Scanner sc) {
		chatClient.chatRoomListRequest(sc);
	}

	private void enterChatRoomMenu(Scanner sc) {
		try {
			chatClient.enterRoomRequest(sc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}