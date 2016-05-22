package authentificationmanager;



import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.query.AuthenticationQuery;
import model.response.AuthenticationServerResponse;
import model.response.AuthenticationServerResponse.Status;



public class ProtocoleHandler {
	

	
	public String authentification(String  login, char[] cs) throws IOException{
		String password="";
		for(int i=0;i<cs.length;i++){
		password=password+cs[i];	
		}
		System.out.println(login);
	
		
		Socket socket = ServeurCommuniction.getS();
		System.out.println(socket.toString());
		
		
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		
	Status statut;	
		
	
	// send the information

	System.out.println(cs);
	AuthenticationQuery query = new AuthenticationQuery(login, password);
	
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String gsonquery = "AUTH "+gson.toJson(query);
	System.out.println(gsonquery);
	
	out.println(gsonquery);


	String message =in.readLine();
	System.out.println(message);

		// Prefix and content detection
		int prefixEnd = message.indexOf(' ');
		
		
		String prefix = message.substring(0, prefixEnd);
		
		if(prefix=="ERR"){
			return "erreur";
		}
				
		String content = message.substring(prefixEnd + 1);
		
		AuthenticationServerResponse autentificaiton = gson.fromJson(content, AuthenticationServerResponse.class);
	System.out.println("le status est "+ autentificaiton.getStatus());
		statut = autentificaiton.getStatus();
		if(statut==null)statut=statut.KO;
		
	return statut.name();
	}

}
