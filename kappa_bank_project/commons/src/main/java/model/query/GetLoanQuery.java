package model.query;

import util.JsonImpl;
import model.query.GetLoanQuery;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetLoanQuery {
	// Attributes
	private String LaonTypeID;

	// constructor
	public GetLoanQuery(String LaonTypeID) {
		super();
		this.LaonTypeID = LaonTypeID;
	}
  
	
	@Override
	public String toString() {
		return "GetLoanQuery " + JsonImpl.toJson(this);
	}
	


	
	// getters and setters
	
	public String getRate_id() {
		return LaonTypeID;
	}

	public void setRate_id(String LaonTypeID) {
		this.LaonTypeID = LaonTypeID;
	}
}
