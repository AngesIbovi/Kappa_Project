package model.response;

public class WorseSimulatedLoanResponse extends ServerResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public WorseSimulatedLoanResponse(String message) {
		super();
		this.message = message;
	}
	public WorseSimulatedLoanResponse() {
		// TODO Auto-generated constructor stub
	}

}
