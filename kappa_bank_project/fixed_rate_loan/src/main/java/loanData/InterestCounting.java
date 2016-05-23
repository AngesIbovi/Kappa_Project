package loanData;

public class InterestCounting {
	
	public float interestPerMonth(float amount, float rate, int nbOfperiodicity)
	{
		float interest= (float) ((amount*(rate*0.01))/nbOfperiodicity);
		
		return interest;
	}

}
