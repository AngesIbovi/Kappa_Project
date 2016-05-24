package model.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 2 - 28/04/2016
 * @author Kappa-V
 */
public class GetLoanServerResponse extends ServerResponse {

	private List<RateList> rate_list = new ArrayList<>();
	// Inner class
 
	public List<RateList> getRate_list() {
		return rate_list;
	}
	public void setRate_list(List<RateList> rate_list) {
		this.rate_list = rate_list;
	}
	public static class RateList {
		private String loan_type_id;
		private String name;
		private float max_duration;
		public RateList(String loan_type_id, String name, float max_duration) {
			this.name = name;
			this.loan_type_id = loan_type_id; 
			this.max_duration = max_duration;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLoan_type_id() {
			return loan_type_id;
		}

		public void setLoan_type_id(String loan_type_id) {
			this.loan_type_id = loan_type_id;
		}

		public float getMax_duration() {
			return max_duration;
		}

		public void setMax_duration(float max_duration) {
			this.max_duration = max_duration;
		}

		@Override
		public String toString() {
			return name;
		}
		
	
		
	}
	
	 
	 
}
