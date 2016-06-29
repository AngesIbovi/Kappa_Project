package controler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;






import model.query.getMaxDurationQuery;
import model.response.GetValueOfRateServerResponse;
import model.response.getIndicatorRateServerResponse;
import model.response.getMaxDurationResponse;

import com.google.gson.Gson;

public class Controler {

	
	
	
	public int getDuration(String name, Socket socket){
		int value= 0;
		try {
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		getMaxDurationQuery duration = new getMaxDurationQuery(name);
		System.out.println("merde"+duration.getName());
		Gson gson=new Gson();
		String query= "GetMaxDuration "+gson.toJson(duration);
		out.println(query);
		String response= in.readLine();
		System.out.println(response);
		
		int prefixEnd = response.indexOf(' ');
		
		String prefix = response.substring(0, prefixEnd);
				String content = response.substring(prefixEnd + 1);
				getMaxDurationResponse repduration= 
					gson.fromJson(content, getMaxDurationResponse.class);
				value = repduration.getMax_duration();
	} catch (Exception e) {
		// TODO: handle exception
	}
	return value;
	
	
	
	
	
	
	}
	
public static float getIndicatorRate(Socket socket,String name){
		
		float value=0;
		
		try {
			
	
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			int rate=0;
		
			
			model.query.getIndicatorRate alltypes = new  model.query.getIndicatorRate(rate);
			alltypes.setName(name);
			Gson gson = new Gson();
			String query = "getIndicatorRate " + gson.toJson(alltypes);
			out.println(query);

			
			String response = in.readLine();

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);

			String content = response.substring(prefixEnd + 1);

			getIndicatorRateServerResponse indiRate = gson.fromJson(content, getIndicatorRateServerResponse.class);
			
			value= indiRate.getRate();
			System.out.println(value);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return value;
		
		
	}	

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	//public int MaxDurationQuery(String Name,Socket socket){

//		getMaxDurationResponse duration = null;
//		
//		try{
//			
//			
//
//			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			
//
//			getMaxDurationQuery maxDuration = new model.query.getMaxDurationQuery(Name);
//	
//			Gson gson = new Gson();
//			String query = "getMaxDurationQuery" + gson.toJson(maxDuration);
//			
//			System.out.println(query);
//			out.println(query);
//
//			
//			// manage the response of the server
//			
//			String response = in.readLine();
//
//			System.out.println(response);
//
//			int prefixEnd = response.indexOf(' ');
//
//		//	String prefix = response.substring(0, prefixEnd);
//			
//
//			String content = response.substring(prefixEnd + 1);
//			
//		
//			duration = gson.fromJson(content, getMaxDurationResponse.class);
//			System.out.println(duration.toString());
//			
//		}catch(Exception e){
//			e.toString();
//		}
//		return duration.getMax_duration();
//		
//	}
//}
