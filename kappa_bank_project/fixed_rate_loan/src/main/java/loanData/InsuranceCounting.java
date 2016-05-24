package loanData;

public class InsuranceCounting {
	
	public float insurancePerMonth(float amount, float rate, int nbOfperiodicity)
	{
		float insurance= (float) ((amount*(rate*0.01))/nbOfperiodicity);
		
		return insurance;
	}

}
