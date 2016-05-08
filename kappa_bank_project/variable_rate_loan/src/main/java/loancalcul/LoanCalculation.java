package loancalcul;

import lendingscenario.GetInformation;
import lendingscenario.Information;

public class LoanCalculation {
	
	public float loan( float capital,int nbr_month)
	{
		float rest;
		float payment=0;
		
		payment=(float)capital/(nbr_month);
		
		System.out.println("--------Calcul du remboursement du prêt sans intérêt---------");
		System.out.println("Le capital: "+capital+" nombre de mois: "+nbr_month+"\n");
		System.out.println("Le payement mesuel est de :"+payment);
		return payment;
	}
	
	
	public float restCapital(float capital,int nbr_month){
		float rest;
		float payment=0;
		
		payment=(float)capital/(nbr_month);
		
		
		return (capital-payment*12);
		
	}
	

}
