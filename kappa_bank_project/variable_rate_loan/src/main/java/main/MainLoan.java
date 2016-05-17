package main;

import java.util.Scanner;

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
		
		
		
		System.out.println("------------------------Bienvenue dans la banque mutuelle -----------------------------");
		System.out.println();
		System.out.println("                A) Tapez 1 pour un scénario normal ");
		System.out.println();
		System.out.println("                B) Tapez 2 pour un scénario favorable");
		System.out.println();
		System.out.println("                B) Tapez 2 pour un scénario défavorable");
		System.out.println("------------------------------------------------------------------------------------");
		System.out.print("votre Choix: ");
		Scanner scanner = new Scanner(System.in);

		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			System.out.println("----------------------------------------------------------------------------");
			System.out.println("--------Calcul du remboursement du prêt sans intérêt------------------------");
			System.out.println("le durée maximale du prêt est de: "+information.getDuration()*12+"mois\n");
			System.out.println("Le capital: "+capital+" nombre de mois: "+nbr_month+"\n");
			System.out.println("Le payement mesuel est de :"+loancalculation.loan(capital, nbr_month));
			
			System.out.println("-----------------Intérêts de la banque -------------------------------------");	
			System.out.println("Le payement mesuel des intérêts sont de :"+calculationofinterest.interest(capital, rate)+" par mois");
			break;
		case 2:

			break;
		case 3:

			break;
		
	}

}}
