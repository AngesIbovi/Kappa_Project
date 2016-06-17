package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @author Kappa-M
 * @version R3 sprint 3 - 20/05/2016
 */
public class GetCustomersQuery implements ClientQuery {
	// Attributes
	private String cust_login;
	
	// toString method
	@Override
	public String toString() {
		return "getCustomers " + JsonImpl.toJson(this);
	}

	// Constructor
	public GetCustomersQuery(String cust_login) {
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
