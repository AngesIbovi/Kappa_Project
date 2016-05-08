package lendingscenario;

public class Information {
	/* type of credit floating rate*/
	 float amount_orrowed;
	private  int duration;
	private  String periodicity;
	private  int value_insurance;
	/*frequency variable rate review*/
	private  int First_time, All_other_times;
	
	public Information(){}
	
	
	@Override
	public String toString() {
		return "Affichage des informations fournis par le client\n"+"Information [amount_orrowed=" + amount_orrowed + ", duration=" + duration + ", periodicity="
				+ periodicity + ", value_insurance=" + value_insurance + ", First_time=" + First_time
				+ ", All_other_times=" + All_other_times + "]";
	}


	public Information(float amount_orrowed, int duration, String periodicity, int value_insurance, int first_time,
			int all_other_times) {
	
		this.amount_orrowed = amount_orrowed;
		this.duration = duration;
		this.periodicity = periodicity;
		this.value_insurance = value_insurance;
		First_time = first_time;
		All_other_times = all_other_times;
	}

	public float getAmount_orrowed() {
	return amount_orrowed;
	}
	
	public void setAmountb_orrowed(float amount_orrowed) {
			this.amount_orrowed = amount_orrowed;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public String getPeriodicity() {
			return periodicity;
		}

		public void setPeriodicity(String periodicity) {
			this.periodicity = periodicity;
		}

		public int getValue_insurance() {
			return value_insurance;
		}

		public void setValue_insurance(int value_insurance) {
			this.value_insurance = value_insurance;
		}

		public int getFirst_time() {
			return First_time;
		}

		public void setFirst_time(int first_time) {
			First_time = first_time;
		}

		public int getAll_other_times() {
			return All_other_times;
		}

		public void setAll_other_times(int all_other_times) {
			All_other_times = all_other_times;
		}

}
