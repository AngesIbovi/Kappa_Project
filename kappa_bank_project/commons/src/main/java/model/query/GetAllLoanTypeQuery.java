package model.query;

import util.JsonImpl;

public class GetAllLoanTypeQuery {
	
	String Name;

	
	public GetAllLoanTypeQuery(String Name)
	{
		this.Name=Name;
	}
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
	return "getAllLoanType "+JsonImpl.toJson(this);
	}

}
