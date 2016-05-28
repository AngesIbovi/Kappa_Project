package model.query;

public class DynamiqueResearchQuery {
	String typeOfLoans;
	int ageRange;
	String date;
	public DynamiqueResearchQuery() {
		super();
	}
	public DynamiqueResearchQuery(String typeOfLoans, int ageRange, String date) {
		super();
		this.typeOfLoans = typeOfLoans;
		this.ageRange = ageRange;
		this.date = date;
	}
	public String getTypeOfLoans() {
		return typeOfLoans;
	}
	public void setTypeOfLoans(String typeOfLoans) {
		this.typeOfLoans = typeOfLoans;
	}
	public int getAgeRange() {
		return ageRange;
	}
	public void setAgeRange(int ageRange) {
		this.ageRange = ageRange;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
