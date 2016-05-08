package main;

import lendingscenario.FloatingRate;
import lendingscenario.GetInformation;
import lendingscenario.Information;
import loancalcul.CalculationOfInterest;
import loancalcul.LoanCalculation;

public class MainLoan {
	
	public static void main(String[] args) {
		
		GetInformation getinformation = new GetInformation();
		Information information = getinformation.Insert();
		FloatingRate floatingrate = new FloatingRate();
		 LoanCalculation loancalculation=new LoanCalculation();
		 CalculationOfInterest calculationofinterest= new CalculationOfInterest();
		 
		 
		 
		 float capital = information.getAmount_orrowed();
		 int nbr_month = information.getDuration()*12; // duration in month
		 int rate = floatingrate.Rate();
		
		
		
		
		
		loancalculation.loan(capital, nbr_month);
		System.out.println("Le reste à payer "+loancalculation.restCapital(capital, nbr_month));
		calculationofinterest.interest(capital, rate);
		
	}

}
