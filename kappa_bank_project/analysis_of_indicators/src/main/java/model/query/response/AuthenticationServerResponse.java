package model.query.response;

/**
 * Server response for the authentication phase. See the protocol's documentation for more details.
 * @author Kappa-V
 * @version R3 sprint 1 - 13/04/2016
 */
public class AuthenticationServerResponse extends NewCustomerServerResponse {
	//Attributes
	private int your_authorization_level;
	private boolean wrong_id;
	
	/**
	 * Constructor for a successful connection
	 * @param your_authorization_level - the authorization level for this user, 
	 * an information given by the database.
	 */
	public AuthenticationServerResponse(int your_authorization_level) {
		super(Status.OK);
		this.your_authorization_level = your_authorization_level;
	}
	
	/**
	 * Constructor for an unsuccessful connection attempt
	 * @param wrong_id - if (wrong_id), the user id provided by the client was not found in the database. 
	 * Else, the user id was found, but the password was wrong.
	 */
	public AuthenticationServerResponse(boolean wrong_id) {
		super(Status.KO);
		this.wrong_id = wrong_id;
		your_authorization_level = -1;
	}

	
	
	
	// Getters and setters
	
	public int getYour_authorization_level() {
		return your_authorization_level;
	}

	public void setYour_authorization_level(int your_authorization_level) {
		this.your_authorization_level = your_authorization_level;
	}

	public boolean isWrong_id() {
		return wrong_id;
	}

	public void setWrong_id(boolean wrong_id) {
		this.wrong_id = wrong_id;
	}
}