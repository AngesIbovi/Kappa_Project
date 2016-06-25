package model.response;

public class RepaymentsResponse extends ServerResponse{


		
		private float totalLoans;
		private float totalRepayments;
		
		public float getTotalLoans() {
			return totalLoans;
		}
		public void setTotalLoans(float totalLoans) {
			this.totalLoans = totalLoans;
		}
		public float getTotalRepayments() {
			return totalRepayments;
		}
		public void setTotalRepayments(float totalRepayments) {
			this.totalRepayments = totalRepayments;
		}
	
		public RepaymentsResponse(float totalLoans, float totalRepayments) {
			super();
			this.totalLoans = totalLoans;
			this.totalRepayments = totalRepayments;
		}
		public RepaymentsResponse() {
			super();
		}

}
