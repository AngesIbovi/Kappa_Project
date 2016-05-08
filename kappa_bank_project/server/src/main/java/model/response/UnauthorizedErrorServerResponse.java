package model.response;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @author Kappa-V
 * @version R3 Sprint 1 - 13/04/2016
 */
public class UnauthorizedErrorServerResponse extends ServerResponse {
	// Attributes
	private boolean connected;
	private int your_authorization_level;
	private int required_authorization_level;
	
	@Override
	public String toString() {
		return "UNAUTHORIZED " + JsonImpl.toJson(this);
	}

	public UnauthorizedErrorServerResponse(boolean connected, int your_authorization_level, int required_authorization_level) {
		super();
		this.connected = connected;
		this.your_authorization_level = your_authorization_level;
		this.required_authorization_level = required_authorization_level;
	}
	
	
	
	// Getters and setters
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public int getYour_authorization_level() {
		return your_authorization_level;
	}

	public void setYour_authorization_level(int your_authorization_level) {
		this.your_authorization_level = your_authorization_level;
	}

	public int getRequired_authorization_level() {
		return required_authorization_level;
	}

	public void setRequired_authorization_level(int required_authorization_level) {
		this.required_authorization_level = required_authorization_level;
	}
}