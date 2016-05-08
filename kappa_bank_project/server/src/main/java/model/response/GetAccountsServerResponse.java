package model.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetAccountsServerResponse extends ServerResponse {
	private List<Account> accounts;
	
	public GetAccountsServerResponse(List<Account> accounts) {
		this.accounts = accounts;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	// Constructor and adder for easier server-side construction
	public GetAccountsServerResponse() {
		this.accounts = new ArrayList<>();
	}
	
	public void addAccount(String account_id, String account_num) {
		accounts.add(new Account(account_id, account_num));
	}

	// Inner class
	public static class Account {
		private String account_id;
		private String account_num;
		
		public Account(String account_id, String account_num) {
			super();
			this.account_id = account_id;
			this.account_num = account_num;
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
	}
}