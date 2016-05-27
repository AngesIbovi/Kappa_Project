package model.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication class. See the protocol's documentation for more details.</br>
 * Contains the Account subclass.
 * @version R3 sprint 3 - 13/05/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-Added a "name" attribute to the Account subclass</br>
 * 			-Added a copy method. It was needed in CustomerChoiceGUI.</br>
 * 			-Added an empty constructor to Account subclass</br>
 * 			-Implemented toString for the Account subclass</br>
 */
public class GetCustomersServerResponse extends ServerResponse {
	private List<Customers> accounts;
	
	public GetCustomersServerResponse(List<Customers> accounts) {
		this.accounts = accounts;
	}

	public List<Customers> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Customers> accounts) {
		this.accounts = accounts;
	}
	
	// Constructor and adder for easier server-side construction
	public GetCustomersServerResponse() {
		this.accounts = new ArrayList<>();
	}
	
	public void addAccount(String account_id, String account_num, String name) {
		accounts.add(new Customers(account_id, account_num, name));
	}

	// Inner class
	public static class Customers {
		private String account_id;
		private String account_num;
		private String name;
		
		public Customers() {
			// Do nothing
		}
		
		public Customers(String account_id, String account_num, String name) {
			super();
			this.account_id = account_id;
			this.account_num = account_num;
			this.name = name;
		}
		
		public String getAccount_id() {
			return account_id;
		}
		public void setAccount_id(String account_id) {
			this.account_id = account_id;
		}
		public String getAccount_num() {
			return account_num;
		}
		public void setAccount_num(String account_num) {
			this.account_num = account_num;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name + "      -     Num. Compte => " + account_num;
		}
	}
}