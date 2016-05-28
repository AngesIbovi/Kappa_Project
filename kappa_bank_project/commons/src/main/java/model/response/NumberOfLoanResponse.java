package model.response;

public class NumberOfLoanResponse extends ServerResponse{
	int numberOfSimulations;
	int numberOfLoans;

	public NumberOfLoanResponse() {
	}

	public NumberOfLoanResponse(int numberOfSimulations, int numberOfLoans) {
		super();
		this.numberOfSimulations = numberOfSimulations;
		this.numberOfLoans = numberOfLoans;
	}

	public int getNumberOfSimulations() {
		return numberOfSimulations;
	}

	public void setNumberOfSimulations(int numberOfSimulations) {
		this.numberOfSimulations = numberOfSimulations;
	}

	public int getNumberOfLoans() {
		return numberOfLoans;
	}

	public void setNumberOfLoans(int numberOfLoans) {
		this.numberOfLoans = numberOfLoans;
	}

}
