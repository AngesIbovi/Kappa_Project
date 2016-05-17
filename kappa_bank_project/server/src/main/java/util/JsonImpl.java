package util;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class is used to configure and access a single Gson object instance for the whole project.
 * @version R2 sprint 1 - 17/03/2016
 * @author Kappa
 */
public class JsonImpl {
	/**
	 * Private empty constructor : makes it impossible to instantiate JsonImpl.
	 */
	private JsonImpl(){}
	
	/**
	 * The Gson object shared by the whole project.
	 */
	private static Gson gson;
	
	/**
	 * Constructor for the Gson object.
	 * @return the correctly configured Gson object, depending on the properties
	 * @throws NullPointerException : if ServerProperties is not initialized first.
	 */
	public static synchronized void init() {
		Properties props = KappaProperties.getInstance();
		if(props.getProperty("PRETTY_PRINT").equals("TRUE")) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new Gson();
		}
	}
	
	
	/**
	 * Converts a Java object into a JSON string. 
	 * @param o : a serializable Java Object
	 * @return : the JSON representation of that object.
	 * 
	 */
	public static String toJson(Object o) {
		return gson.toJson(o);
	}
	
	/**
	 * Deserializes a JSON string.
	 * @param jsonString : the JSON representation of a Java object
	 * @param classOfT : the expected class of the object to be deserialized.
	 * @return The Java Object of the right class, or null if jsonString==null
	 * @throws JsonSyntaxException if jsonString is not properly formatted JSON.
	 */
	public static <T> T fromJson(String jsonString, Class<T> classOfT) {
		return gson.fromJson(jsonString, classOfT);
	}
}
