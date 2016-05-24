package model.response;

public class SignLoanServerResponse extends ServerResponse {
	// Inner enum
	public enum Status {
		OK,
		KO
	}
	
	private final Status status;

	public SignLoanServerResponse(Status status) {
		super();
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
}
