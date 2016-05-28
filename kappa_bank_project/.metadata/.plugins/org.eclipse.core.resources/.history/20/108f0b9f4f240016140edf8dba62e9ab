package loan;

import java.io.FileInputStream;
import java.net.Socket;
import java.util.Properties;

public class ServeurCommuniction {

	

	public static Socket getS() {

		Properties properties = new Properties();
		String propFileName = "server.properties";

		try {
			FileInputStream inputStream = new FileInputStream(propFileName);
			properties.load(inputStream);
			String adress = properties.getProperty("SERVER_ADRESS");
			int port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
		

			return new Socket(adress, port);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	


}
