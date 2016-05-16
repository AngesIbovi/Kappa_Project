package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 1 - 18/04/2016
 * @author Kappa-V
 */
public class AuthenticationQuery implements ClientQuery {
	// Attributes
	private String id;
	private String password;
	
	public AuthenticationQuery(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}
	
	// toString method
	@Override
	public String toString() {
		return "AUTH " + JsonImpl.toJson(this);
	}
	
	
	
	// Getters and Setters
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}