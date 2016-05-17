package loancalcul;

import lendingscenario.FloatingRate;
import lendingscenario.GetInformation;
import lendingscenario.Information;

public class CalculationOfInterest {
	
	
	public float interest(float amount, int rate)
	{
		float interest;
	System.out.println("montant"+amount+"\n taux:"+ rate);
		interest= (float) (amount*((rate*0.01)/12));
		
	System.out.println("--------------Intérêts de la banque --------------");	
	System.out.println("Le payement mesuel des inérêts sont de :"+interest+" par mois");
		return interest;
	}

	

}
