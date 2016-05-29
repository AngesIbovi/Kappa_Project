package model.response;

public class MustSimulatedLoanResponse extends ServerResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MustSimulatedLoanResponse(String message) {
		super();
		this.message = message;
	}
	public MustSimulatedLoanResponse() {
		// TODO Auto-generated constructor stub
	}

}
