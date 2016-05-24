package model.query;

import util.JsonImpl;

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
