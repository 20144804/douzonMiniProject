package client;

import java.io.IOException;
import java.util.Properties;

public class ClientProperties {
	static Properties prop = new Properties();
	static {
        try {
            prop.load(ClientProperties.class.getResourceAsStream("cp.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static int getPort() {
        return Integer.parseInt(prop.getProperty("server.port", "50001"));
    }
    
    public static String getFilePath() {
        return prop.getProperty("member.file.path");
    }
}