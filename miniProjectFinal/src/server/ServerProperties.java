package server;

import java.io.IOException;
import java.util.Properties;

public class ServerProperties {
	static Properties prop = new Properties();
	static {
        try {
            prop.load(ServerProperties.class.getResourceAsStream("sp.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static int getPort() {
        return Integer.parseInt(prop.getProperty("server.port", "50001"));
    }
    
    public static String getWorkPath() {
        return prop.getProperty("work.path");
    }
    
    public static int getThreadPoolSize() {
        return Integer.parseInt(prop.getProperty("thread.poll.size", "100"));
    }
    
    public static String getMemberFileName() {
        return prop.getProperty("member.file.name");
    }
}