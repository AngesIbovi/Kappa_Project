package model.response;

import java.util.ArrayList;

public class GetAllAcountsServerResponse extends ServerResponse {

	public ArrayList<String> array;
	
	
	public GetAllAcountsServerResponse() {
		// TODO Auto-generated constructor stub
	}
	public GetAllAcountsServerResponse(ArrayList<String> array) {
		// TODO Auto-generated constructor stub
		this.array = array;
	}



	public ArrayList<String> getArray() {
		return array;
	}
	
	public void dispalay(){
		for (String string : array) {
			System.out.println(string);
		}
	}


	public void setArray(ArrayList<String> array) {
		this.array = array;
	}
	
	
}
