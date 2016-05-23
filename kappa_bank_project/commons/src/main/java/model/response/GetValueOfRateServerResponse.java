package model.response;

import java.util.ArrayList;

public class GetValueOfRateServerResponse extends ServerResponse{
	
	float rate;
	
public GetValueOfRateServerResponse(){
		
	}
	
	public GetValueOfRateServerResponse(float rate){
		this.rate=rate;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	
	

}
