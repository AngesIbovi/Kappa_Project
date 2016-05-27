package model.query;

public class LoansQuery {
	

	String is_real;
	String account_id;
	String loan_type;
	String effective_date;
	float capital;
	float remainingowedcapital;
	int repayment_frequency;
	float remaining_repayment;
	float repayment_constant;
	String rate_nature;
	String amortization_type;
	String name;
	float insurance;
	float progressing_fee;
	int duration;
	
	
	public  LoansQuery(String is_real,String account_id,String loan_type,String effective_date,float capital,float remainingowedcapital,int repayment_frequency,float remaining_repayment, float repayment_constant,String rate_nature,String amortization_type,String name,float insurance,float progressing_fee,int duration)
	{
		this.is_real=is_real;
		this.account_id=account_id;
		this.loan_type=loan_type;
		this.effective_date=effective_date;
		this.capital=capital;
		this.remainingowedcapital=remainingowedcapital;
		this.repayment_frequency=repayment_frequency;
		this.remaining_repayment=remaining_repayment;
		this.repayment_constant= repayment_constant;
		this.rate_nature=rate_nature;
		this.amortization_type=amortization_type;
		this.name=name;
		this.insurance=insurance;
		this.progressing_fee=progressing_fee;
		this.duration=duration;
	}



	public String getIs_real() {
		return is_real;
	}



	public void setIs_real(String is_real) {
		this.is_real = is_real;
	}



	public String getAccount_id() {
		return account_id;
	}



	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}



	public String getLoan_type() {
		return loan_type;
	}



	public void setLoan_type_id(String loan_type) {
		this.loan_type = loan_type;
	}



	public String getEffective_date() {
		return effective_date;
	}



	public void setEffective_date(String effective_date) {
		this.effective_date = effective_date;
	}



	public float getCapital() {
		return capital;
	}



	public void setCapital(float capital) {
		this.capital = capital;
	}



	public float getRemainingowedcapital() {
		return remainingowedcapital;
	}



	public void setRemainingowedcapital(float remainingowedcapital) {
		this.remainingowedcapital = remainingowedcapital;
	}



	public int getRepayment_frequency() {
		return repayment_frequency;
	}

	public void setRepayment_frequency(int repayment_frequency) {
		this.repayment_frequency = repayment_frequency;
	}



	public float getRemaining_repayment() {
		return remaining_repayment;
	}



	public void setRemaining_repayment(float remaining_repayment) {
		this.remaining_repayment = remaining_repayment;
	}



	public String getRate_nature() {
		return rate_nature;
	}



	public void setRate_nature(String rate_nature) {
		this.rate_nature = rate_nature;
	}



	public String getAmortization_type() {
		return amortization_type;
	}



	public void setAmortization_type(String amortization_type) {
		this.amortization_type = amortization_type;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public float getInsurance() {
		return insurance;
	}



	public void setInsurance(float insurance) {
		this.insurance = insurance;
	}



	public float getProgressing_fee() {
		return progressing_fee;
	}



	public void setProgressing_fee(float progressing_fee) {
		this.progressing_fee = progressing_fee;
	}



	public int getDuration() {
		return duration;
	}



	public void setDuration(int duration) {
		this.duration = duration;
	}



	public float getRepayment_constant() {
		return repayment_constant;
	}



	public void setRepayment_constant(float repayment_constant) {
		this.repayment_constant = repayment_constant;
	}



	public void setLoan_type(String loan_type) {
		this.loan_type = loan_type;
	}



	@Override
	public String toString() {
		return "Loans [is_real=" + is_real + ", account_id=" + account_id + ", loan_type=" + loan_type
				+ ", effective_date=" + effective_date + ", capital=" + capital + ", remainingowedcapital="
				+ remainingowedcapital + ", repayment_frequency=" + repayment_frequency + ", remaining_repayment="
				+ remaining_repayment + ", rate_nature=" + rate_nature + ", amortization_type=" + amortization_type
				+ ", name=" + name + ", insurance=" + insurance + ", progressing_fee=" + progressing_fee + ", duration="
				+ duration + "]";
	}
	
	
	
}
