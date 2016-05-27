package model.response;

import java.util.ArrayList;
import java.util.List;

public class GetAccountsServerResponseMO extends ServerResponse {
	private List<Account> accounts;
	
	public GetAccountsServerResponseMO(List<Account> accounts) {
		this.accounts = accounts;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	// Constructor and adder for easier server-side construction
	public GetAccountsServerResponseMO() {
		this.accounts = new ArrayList<>();
	}
	
	public void addAccount(String account_id, String account_num, String name) {
		accounts.add(new Account(account_id, account_num, name));
	}

	// Inner class
	public static class Account {
		private String account_id;
		private String account_num;
		private String name;
		
		public Account() {
			// Do nothing
		}
		
		public Account(String account_id, String account_num, String name) {
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
			return "id_compte: " + account_id + "             " + "Name: " + name + "             " + "Numéro de commpte: " +account_num;
		}
	}
}