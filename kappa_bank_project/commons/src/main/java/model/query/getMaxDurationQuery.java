package model.query;

import util.JsonImpl;

public class getMaxDurationQuery {
	private String Name;
	
	
	//Constructor
	public getMaxDurationQuery(String name){
		super();
		this.Name=name;
		
	}

	

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}



	@Override
	public String toString() {
		return "getMaxDuration "+JsonImpl.toJson(this);
	}



	

	

}
