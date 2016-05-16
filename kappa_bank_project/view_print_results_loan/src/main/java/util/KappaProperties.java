package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	//Just to Debug will try to read the properties's file contain
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	/**
	 * Initializes the class.
	 * @throws IOException : if the properties file can't be found
	 */
	public static void init() throws IOException {
		//Loading properties
		FileInputStream fin = new FileInputStream(propPath);
		
		//get the propPath contain
		String content = readFile(propPath, StandardCharsets.UTF_8);
		//show the propPath contain
		//System.out.print(content);
		bean = new Properties();
		bean.load(fin);
		fin.close();
	}
}
