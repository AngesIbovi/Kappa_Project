package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 3 - 08/05/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-Renamed SearchAccountsQuery, from GetAccountsQuery, to signify it is the secondary method for looking up accounts.
 */
public class SearchAccountsQuery implements ClientQuery {
	// Attributes
	private String firstName;
	private String lastName;
	private boolean myCustomers;
	
	// toString method
	@Override
	public String toString() {
		return "searchAccounts " + JsonImpl.toJson(this);
	}

	//constructor
	public SearchAccountsQuery(String firstName, String lastName, boolean myCustomers) {
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
