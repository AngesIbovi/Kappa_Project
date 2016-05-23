package model.query;

import util.JsonImpl;

public class GetAllAccountsQuery {
	
private String message;

public GetAllAccountsQuery(String message) {
	this.message = message;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}
@Override
	public String toString() {
		// TODO Auto-generated method stub
	return "getAllAccounts "+JsonImpl.toJson(this);
	}
}
