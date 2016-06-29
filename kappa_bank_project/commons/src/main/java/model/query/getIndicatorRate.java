package model.query;

import util.JsonImpl;

public class getIndicatorRate {
	public String name; 
	public float rate;
	public String Loan_type_Id;
	
	
	public getIndicatorRate(String name, float rate, String Loan_type_Id) {
		super();
		this.name = name;
		this.rate = rate;
		this.Loan_type_Id=Loan_type_Id;
	}
	
	public getIndicatorRate(float rate){
		this.rate= rate;
		
	}


	public String getName() {
		return name;
	}


	public String getLoan_type_Id() {
		return Loan_type_Id;
	}


	public void setLoan_type_Id(String loan_type_Id) {
		Loan_type_Id = loan_type_Id;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getRate() {
		return rate;
	}


	public void setRate(float rate) {
		this.rate = rate;
	}


	@Override
	public String toString() {
	
		
		return "getIndicatorRate "+JsonImpl.toJson(this);
	} 
	

	
	
	
}
