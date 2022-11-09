package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import lombok.Data;

public class DBTest13 {
	public static String getCapitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
	
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("db.properties"));
			
			Class.forName(prop.getProperty("driverClass"));
			System.out.println("JDBC 드라이버 로딩 성공");
			
			Connection conn1 = DriverManager.getConnection(prop.getProperty("dbServerConn")
					, prop.getProperty("dbUser")
					, prop.getProperty("dbPasswd"));
			
			System.out.println("DB 서버에 연결됨");

			PreparedStatement pstmt = conn1.prepareStatement("select * from boards");
			
			List<Object> list = new ArrayList<>();
			
			//쿼리 실행 
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			Class cls = Class.forName(prop.getProperty("boardClass"));
			final int columnCount = rsmd.getColumnCount();
			Map<String, Method> methodMap = new HashMap<>();
			
			for (int i=1;i<=columnCount;i++) {
				//컬럼명을 얻는다
				String columnName = rsmd.getColumnName(i);
				System.out.println(columnName + " : " + rsmd.getColumnType(i));
				switch(rsmd.getColumnType(i)) {
				case Types.INTEGER:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), int.class));
					break;
				case Types.VARCHAR:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), String.class));
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), Date.class));
					break;
				case Types.CLOB:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), String.class));
					break;
				case Types.LONGVARBINARY:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), byte[].class));
					break;
				default:
					methodMap.put(columnName, cls.getMethod("set" + getCapitalize(columnName), String.class));
					break;
				}
			}
			
			while(rs.next()) {
				Object board = cls.newInstance();
				
				for (int i=1;i<=columnCount;i++) {
					String columnName = rsmd.getColumnName(i);
					Method method = methodMap.get(columnName);
					System.out.println("columnName : " + columnName + "   " + method);
					if (method != null) {
						switch(rsmd.getColumnType(i)) {
						case Types.INTEGER:
							method.invoke(board, rs.getInt(columnName));
							break;
						case Types.VARCHAR:
							method.invoke(board, rs.getString(columnName));
							break;
						case Types.DATE:
						case Types.TIME:
						case Types.TIMESTAMP:
							method.invoke(board, rs.getDate(columnName));
							break;
						case Types.CLOB:
							method.invoke(board, rs.getString(columnName));
							break;
						case Types.LONGVARBINARY:
							Blob blob = rs.getBlob(columnName);
							if (blob != null) {
								InputStream in = blob.getBinaryStream();
								byte [] data = new byte[(int)blob.length()];
								in.read(data);
								method.invoke(board, data);
							}
							break;
						default:
							method.invoke(board, rs.getString(columnName));
							break;
						}
					}
				}
				
//				board.setBtitle(rs.getString("btitle"));
//				board.setBcontent(rs.getString("bcontent"));
//				board.setBwrite(rs.getString("bwriter"));
//				board.setBdate(rs.getDate("bdate"));
//				board.setBfilename(rs.getString("bfilename"));
//				
//				Blob blob = rs.getBlob("bfiledata");
//				if (blob != null) {
//					InputStream in = blob.getBinaryStream();
//					byte [] bfiledata = new byte[(int)blob.length()];
//					in.read(bfiledata);
//					board.setBfiledata(bfiledata);
//				}
				list.add(board);
			}
			rs.close();
			pstmt.close();
			conn1.close();
			System.out.println("DB 서버 연결 종료");
			
			if (list.size() == 0) {
				System.out.println("자료가 존재하지 않습니다");
			} else {
				for (Object board : list) {

				}
				System.out.println(list);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
