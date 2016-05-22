package model.query;

import util.JsonImpl;

public class GetValueOfRateQuery {
	
	int rate;
	
	public GetValueOfRateQuery(int rate)
	{
		this.rate=rate;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
	return "GetValueOfRate "+JsonImpl.toJson(this);
	}

}
