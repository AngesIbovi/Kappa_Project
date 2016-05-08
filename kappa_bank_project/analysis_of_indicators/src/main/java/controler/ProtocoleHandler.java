package controler;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;

import loan.ServeurCommuniction;
import model.query.response.AuthenticationQuery;
import model.query.response.AuthenticationServerResponse;
import model.query.response.NewCustomerServerResponse.Status;
import serialization.JsonImpl;

public class ProtocoleHandler {
	

	
	public String authentification(String  login, String password) throws IOException{
		System.out.println(login);
		System.out.println(password);
		
		Socket socket = ServeurCommuniction.getS();
		System.out.println(socket.toString());
		
		
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		
	Status statut;	
		
	
	// send the information
	System.out.println(login);
	System.out.println(password);
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
		String content = message.substring(prefixEnd + 1);
		
		AuthenticationServerResponse autentificaiton = gson.fromJson(content, AuthenticationServerResponse.class);
	
		statut = autentificaiton.getStatus();
	return statut.name();
	}

}
