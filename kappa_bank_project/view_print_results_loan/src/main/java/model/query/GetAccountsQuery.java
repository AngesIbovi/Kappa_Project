package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetAccountsQuery {
	// Attributes
	private String firstName;
	private String lastName;
	private boolean myCustomers;
	
	// toString method
	@Override
	public String toString() {
		return "getAccounts " + JsonImpl.toJson(this);
	}

	//constructor
	public GetAccountsQuery(String firstName, String lastName, boolean myCustomers) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.myCustomers = myCustomers;
	}
	
	
	
	// getters and setters

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isMyCustomers() {
		return myCustomers;
	}

	public void setMyCustomers(boolean myCustomers) {
		this.myCustomers = myCustomers;
	}
	
	
}
