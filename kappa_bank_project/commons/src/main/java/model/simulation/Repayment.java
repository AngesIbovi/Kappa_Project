package model.simulation;

import java.sql.Date;

/**
 * Repayments are used to visualize a simulation's outcome
 * @author Kappa-V
 */
public class Repayment {
	private Date date;
	private float capital;
	private float interest;
	private float insurance;
	private  String id_loan ;

	
	public String getId_loan() {
		return id_loan;
	}


	public void setId_loan(String id_loan) {
		this.id_loan = id_loan;
	}


	public Repayment(Date date, float capital, float interest, float insurance, String id_loan) {
		super();
		this.date = date;
		this.capital = capital;
		this.interest = interest;
		this.insurance = insurance;
		this.id_loan = id_loan;
	}


	public Repayment(Date date, float capital, float interest, float insurance) {
		super();
		this.date = date;
		this.capital = capital;
		this.interest = interest;
		this.insurance = insurance;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getCapital() {
		return capital;
	}

	public void setCapital(float capital) {
		this.capital = capital;
	}

	public float getInterest() {
		return interest;
	}

	public void setInterest(float interest) {
		this.interest = interest;
	}

	public float getInsurance() {
		return insurance;
	}

	
	@Override
	public String toString() {
		return "Repayment [date=" + date + ", capital=" + capital + ", interest=" + interest + ", insurance="
				+ insurance + ", id_loan=" + id_loan + "]";
	}


	public void setInsurance(float insurance) {
		this.insurance = insurance;
	}
}