package loanData;

public class LoanCounting {

	
	public float paymentPerMonth( float capital,int nbr_month)
	{
		float paymentPerMonth = 0;	
		paymentPerMonth=(float)capital/(nbr_month);
		return paymentPerMonth;
	}

}
