package model.query;

import util.JsonImpl;

public class evolutionOfTheSimulationsQuery  implements ClientQuery {
	String date;
	
	public evolutionOfTheSimulationsQuery(String date){
		this.date = date;
	}

	@Override
	public String toString() {
		 return "evolutionOfTheSimulations " + JsonImpl.toJson(this);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
