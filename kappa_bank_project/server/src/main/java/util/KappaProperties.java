package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used to access the same Properties object throughout the application.
 * @version R3 sprint 1 - 18/04/2016
 * @changes 
 * 		R2 Sprint 1 -> R3 Sprint 1 : bean = null. Renamed KappaProperties
 * @author Kappa
 */
public class KappaProperties {
	/**
	 * Private empty constructor : makes it impossible to instantiate ServerProperties.
	 */
	private KappaProperties(){}

	/**
	 * Path to the properties file
	 */
	private static final String propPath = "kappa.properties";
	
	/**
	 * This is the Properties object which will be shared by all other classes who need it.
	 */
	private static Properties bean = null;
	
	/**
	 * Accesses the server's Properties object. 
	 * @return the same Properties object every single time.
	 */
	public static Properties getInstance() {
		return bean;
	}
	
	/**
	 * Initializes the class.
	 * @throws IOException : if the properties file can't be found
	 */
	public static void init() throws IOException {
		//Loading properties
		FileInputStream fin = new FileInputStream(propPath);
		bean = new Properties();
		bean.load(fin);
		fin.close();
	}
}
