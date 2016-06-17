package model.response;

import java.util.ArrayList;

public class EvolutionOfTheSimulationsResponse extends ServerResponse {
	
	public ArrayList<ListResult> array = new ArrayList<>();
	
	
	
	public static class ListResult{
		private int count;
		private String date;
		
		public  ListResult(String date, int count) {
			this.count = count;
			this.date = date;
		}

		public int getCount() {
			return count;
		}
		public ListResult(){
			
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	
	}
	
	public EvolutionOfTheSimulationsResponse(ArrayList<ListResult> array) {
		this.array = array;

	}

	public EvolutionOfTheSimulationsResponse() {
	
	}

	public ArrayList<ListResult> getArray() {
		return array;
	}

	public void setArray(ArrayList<ListResult> array) {
		this.array = array;
	}


}
