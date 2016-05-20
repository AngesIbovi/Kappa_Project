package model;

import java.net.Socket;

public class SessionInformation {
	public final int authorization_level;
	public final String user_id;
	public final Socket S;
	
	public SessionInformation(int authorization_level, String user_id, Socket S) {
		super();
		this.authorization_level = authorization_level;
		this.user_id = user_id;
		this.S = S;
	}

	public int getAuthorization_level() {
		return authorization_level;
	}
	public String getUser_id() {
		return user_id;
	}

	public Socket getSocket() {
		return S;
	}
}
