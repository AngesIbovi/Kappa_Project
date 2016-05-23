package loanClient;

public class ClientData {

	 float loan_requested;
	 int duration;
	 int periodicity;
	 float insurance;
	 float fix_Rate;
	public static int application_fee = 50;

	public ClientData(float loan_requested, int duration, int periodicity, float insurance, float fix_rate) {
	
		this.loan_requested = loan_requested;
		this.duration = duration;
		this.periodicity = periodicity;
		this.insurance = insurance;
		this.fix_Rate = fix_rate;
		
	}

	public float getloan_requested() {
	return loan_requested;
	}
	
	public void setAmountb_orrowed(float loan_requested) {
			this.loan_requested = loan_requested;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public int getPeriodicity() {
			return periodicity;
		}

		public void setPeriodicity(int periodicity) {
			this.periodicity = periodicity;
		}

		public float getinsurance() {
			return insurance;
		}

		public void setinsurance(float insurance) {
			this.insurance = insurance;
		}
		
		public float getFix_Rate() {
			return fix_Rate;
		}

		public void setFix_Rate(int fix_Rate) {
			this.fix_Rate = fix_Rate;
		}


		@Override
		public String toString() {
			return "Affichage des informations fournis par le client\n"+"Information [crédit demandé = " + loan_requested + ", durée de paiement = " + duration + ", nombre de mensualité = "
					+ periodicity + ", assurance=" + insurance + "]";
		}
		
}
