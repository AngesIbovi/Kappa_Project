package model.query;

public class WorseSimulatedLoanQuery  implements ClientQuery{
	private String message;

	public WorseSimulatedLoanQuery(String message) {
		
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public WorseSimulatedLoanQuery(){
	
	}

}
