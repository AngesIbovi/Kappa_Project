package model.query;

import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetSimQuery implements ClientQuery {
	// Attributes
	private String sim_id;
	
	// toString method
	@Override
	public String toString() {
		return "getSim " + JsonImpl.toJson(this);
	}

	// constructor
	public GetSimQuery(String sim_id) {
		super();
		this.sim_id = sim_id;
	}

	
	// getters and setters
	
	public String getSim_id() {
		return sim_id;
	}

	public void setSim_id(String account_id) {
		this.sim_id = account_id;
	}
}
