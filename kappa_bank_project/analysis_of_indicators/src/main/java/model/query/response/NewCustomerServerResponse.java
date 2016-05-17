package model.query.response;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 1 - 13/04/2016
 * @author Kappa-V
 * @changes
 * 		R1 sprint 4 -> R3 sprint 1 : </br>
 * 			-removed the toString method. It is instead implemented by the superclass ServerResponse.
 */
public class NewCustomerServerResponse extends ServerResponse {
	public enum Status {
		OK,
		KO
	}
	
	private Status status;
	
	public NewCustomerServerResponse(Status status) {
		this.status = status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
}
