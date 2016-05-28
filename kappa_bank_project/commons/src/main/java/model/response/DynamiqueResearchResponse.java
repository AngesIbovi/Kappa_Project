package model.response;

import java.util.ArrayList;

import model.response.SumOfInterestResponse.Interest;

public class DynamiqueResearchResponse extends ServerResponse {
	
	ArrayList<SumInterest> array = new ArrayList<SumInterest>();
	
	public ArrayList<SumInterest> getArray() {
		return array;
	}

	public void setArray(ArrayList<SumInterest> array) {
		this.array = array;
	}

	public static class SumInterest {
		String typeOfLoans;
		float sum;
		String date;

		public String getTypeOfLoans() {
			return typeOfLoans;
		}

		public void setTypeOfLoans(String typeOfLoans) {
			this.typeOfLoans = typeOfLoans;
		}

		public float getSum() {
			return sum;
		}

		public float setSum(float sum) {
			return this.sum = sum;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public SumInterest(String typeOfLoans, float f, String date) {
			super();
			this.typeOfLoans = typeOfLoans;
			this.sum = f;
			this.date = date;
		}

		public SumInterest() {
			super();
		}

	}
}