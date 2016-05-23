package model.response;

import java.util.ArrayList;

public class GetAllLoanTypeServerReponse extends ServerResponse{
	
	public ArrayList<String> array;
	
	public GetAllLoanTypeServerReponse(){
		
	}
	
	public GetAllLoanTypeServerReponse(ArrayList<String> array){
		this.array=array;
	}

	public ArrayList<String> getArray() {
		return array;
	}

	public void setArray(ArrayList<String> array) {
		this.array = array;
	}
	
	

}
