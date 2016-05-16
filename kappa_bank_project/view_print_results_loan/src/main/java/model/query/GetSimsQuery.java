package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetSimsQuery {
	// Attributes
	private String account_id;
	
	// toString method
	@Override
	public String toString() {
		return "getSims " + JsonImpl.toJson(this);
	}

	// constructor
	public GetSimsQuery(String account_id) {
		super();
		this.account_id = account_id;
	}

	
	// getters and setters
	
	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
}
