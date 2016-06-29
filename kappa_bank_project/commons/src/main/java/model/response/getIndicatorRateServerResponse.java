package model.response;

public class getIndicatorRateServerResponse extends ServerResponse {

	public float rate; 
	
	
	public getIndicatorRateServerResponse(){
		
		
	}
	
public getIndicatorRateServerResponse(float rate){
		
		this.rate=rate;	
		
}

public float getRate() {
	return rate;
}

public void setRate(float rate) {
	this.rate = rate;
}

	
}
