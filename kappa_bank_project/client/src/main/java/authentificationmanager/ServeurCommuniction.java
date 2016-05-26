package authentificationmanager;



import java.io.FileInputStream;
import java.net.Socket;
import java.util.Properties;

import util.KappaProperties;

public class ServeurCommuniction {
	private static Socket S = null;
	

	public static synchronized Socket getS() {
		if(S!=null)
			return S;
		
		
		Properties properties = KappaProperties.getInstance();

		try {
			String adress = properties.getProperty("SERVER_ADRESS");
			int port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
		

			return new Socket(adress, port);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	


}
