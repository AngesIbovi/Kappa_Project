package model.response;

import java.util.ArrayList;

public class AverageDurationResponse extends ServerResponse{
	
	private ArrayList<AverageClass> array = new ArrayList<AverageClass>();
	
	
	public ArrayList<AverageClass> getArray() {
		return array;
	}


	public void setArray(ArrayList<AverageClass> array) {
		this.array = array;
	}


	public static class AverageClass{
		public AverageClass() {
			// TODO Auto-generated constructor stub
		}
		private String name;
		private float average;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float getAverage() {
			return average;
		}
		public void setAverage(float average) {
			this.average = average;
		}
		public AverageClass(String name, float average) {
			super();
			this.name = name;
			this.average = average;
	}
	
	}
	
	

}
