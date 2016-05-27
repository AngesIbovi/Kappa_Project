package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 4 - 24/05/2016
 * @author Kappa-V
 */
public class SignLoanQuery implements ClientQuery {
	private final String simId;
	private final String password;
	
	public SignLoanQuery(String simId, String password) {
		this.simId = simId;
		this.password = password;
	}

	public String getSimId() {
		return simId;
	}

	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "signLoan " + JsonImpl.toJson(this);
	}
}
