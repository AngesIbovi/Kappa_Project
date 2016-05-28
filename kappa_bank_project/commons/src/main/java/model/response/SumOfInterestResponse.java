package model.response;

import java.util.ArrayList;

public class SumOfInterestResponse extends ServerResponse{
	ArrayList<Interest> array = new ArrayList<Interest>();
	
	
	public ArrayList<Interest> getArray() {
		return array;
	}


	public void setArray(ArrayList<Interest> array) {
		this.array = array;
	}


	public static class Interest{
		String date,  name;
		float sum;
		public Interest(String date, String name, float f) {
			this.date = date;
			this.name= name;
			this.sum= f;
		}
		public Interest() {
			// TODO Auto-generated constructor stub
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float getSum() {
			return sum;
		}
		public void setSum(float sum) {
			this.sum = sum;
		}
		
	}

}
