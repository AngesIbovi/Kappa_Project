package model.response;

public class SignLoanServerResponse extends ServerResponse {
	// Inner enum
	public enum Status {
		OK,
		KO
	}

	private final Status status;
	private final Status password;

	public SignLoanServerResponse(Status status, Status password) {
		super();
		this.status = status;
		this.password = password;
	}

	public Status getStatus() {
		return status;
	}
	
	public Status getPassword() {
		return password;
	}
}
