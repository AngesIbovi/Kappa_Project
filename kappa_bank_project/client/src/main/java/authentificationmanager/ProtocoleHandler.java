package authentificationmanager;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.SessionInformation;
import model.query.AuthenticationQuery;
import model.response.AuthenticationServerResponse;
import model.response.ErrorServerResponse;
import serialization.JsonImpl;



public class ProtocoleHandler {

	/**
	 * Attempts to log in.
	 * @param login
	 * @param pw : the user's password
	 * @return : the session information bean if successful, null if unsuccessful
	 * @throws IOException
	 */
	public static SessionInformation authentification(String  login, char[] pw) throws IOException {
		String password = String.valueOf(pw);

		Socket socket = ServeurCommuniction.getS();
		if(socket == null)
			throw new IOException("No socket");

		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// send the information
		AuthenticationQuery query = new AuthenticationQuery(login, password);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		out.println(query);


		String message =in.readLine();

		// Prefix and content detection
		int prefixEnd = message.indexOf(' ');


		String prefix = message.substring(0, prefixEnd);
		String content = message.substring(prefixEnd + 1);

		if(prefix=="ERR"){
			throw new IOException(JsonImpl.fromJson(content, ErrorServerResponse.class).getMessage());
		}
		
		AuthenticationServerResponse response = gson.fromJson(content, AuthenticationServerResponse.class);
		if(AuthenticationServerResponse.Status.KO.equals(response.getStatus()))
			return null;
		
		return new SessionInformation(response.getYour_authorization_level(), login, socket);
	}
}