package model.response;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R1 sprint 4 - 06/02/2016
 * @author Kappa-V
 */
public class ErrorServerResponse extends ServerResponse {
	private String message;
	
	public ErrorServerResponse(String message) {
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ERR " + JsonImpl.toJson(this);
	}
}
