package model.response;

public class worseSimulatedLoanResponse extends ServerResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public worseSimulatedLoanResponse(String message) {
		super();
		this.message = message;
	}
	public worseSimulatedLoanResponse() {
		// TODO Auto-generated constructor stub
	}

}
