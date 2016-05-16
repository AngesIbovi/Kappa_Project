package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @author Kappa-V
 * @version R3 sprint 3 - 08/05/2016
 */
public class GetAccountsQuery implements ClientQuery {
	// Attributes
	private String cust_login;
	
	// toString method
	@Override
	public String toString() {
		return "getAccounts " + JsonImpl.toJson(this);
	}

	// Constructor
	public GetAccountsQuery(String cust_login) {
		super();
		this.cust_login = cust_login;
	}

	// Getters and setters
	public String getCust_login() {
		return cust_login;
	}
	public void setCust_login(String cust_login) {
		this.cust_login = cust_login;
	}
}
