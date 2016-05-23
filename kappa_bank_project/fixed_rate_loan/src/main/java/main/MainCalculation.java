package main;

import java.util.Scanner;

import loanClient.ClientData;
import loanClient.GetClientData;
import loanData.*;

public class MainCalculation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		loanCalculation();
	}
	
	public static void loanCalculation () {
		Scanner sc = new Scanner(System.in);
		System.out.println("Le montant de frais de dossier est fixé à :"+ClientData.application_fee);
		System.out.println("Si vous souhaitez calculer votre crédit en utilisant la méthode \"CONSTANTE\" tapez 1 : " );
		System.out.println("Si vous souhaitez calculer votre crédit en utilisant la méthode \"DEGRESSIVE\" tapez 2: ");

		int methodNumber = sc.nextInt();


		switch (methodNumber) {
		case 1:
		{
			System.out.println("Calcul du pret avec la methode Constante");
			ClientData information = GetClientData.Insert();
			float capital = information.getloan_requested();
			int nbr_month = information.getDuration()*12; // duration in month
			int nbr_periodicity = information.getPeriodicity(); 
			float rate = information.getFix_Rate();
			float insuranceRate = information.getinsurance();

			LoanCounting loanCounting = new LoanCounting();
			InterestCounting interestCounting= new InterestCounting();
			InsuranceCounting insuranceCounting= new InsuranceCounting();

			float paymentPerMonth = loanCounting.paymentPerMonth(capital, nbr_periodicity);
			float interestPerMonth = interestCounting.interestPerMonth(capital, rate, nbr_periodicity);
			float insurancePerMonth = insuranceCounting.insurancePerMonth(capital, insuranceRate, nbr_periodicity);
			float totalPaymentPerMonth = paymentPerMonth + interestPerMonth + insurancePerMonth;

			//			System.out.println("Le montant du crédit initial à payer par mois: "+paymentPerMonth);
			//			System.out.println("Le montant d'interet à payer par mois: "+interestPerMonth);
			//			System.out.println("Le frais d'assurance à payer par mois: "+insurancePerMonth);
			System.out.println("****************");
			System.out.println("********Compute the interest by the constant method********");
			float total = totalPaymentPerMonth * nbr_periodicity;
			float totalToPay = total;
			for (int i =1;i<=nbr_periodicity;i++)
			{
				total-=totalPaymentPerMonth;
				System.out.println("La mentualite du mois N"+i+" est "+totalPaymentPerMonth+"€, le taux d'interret est: "+rate+" %, le capital restant est : " +total+"€");
			}
			System.out.println("La somme totale à rendre: "+totalToPay+" €");
			break;
		}
		case 2: 
		{
			System.out.println("Calcul du pret avec la methode Degressive");
			ClientData information = GetClientData.Insert();
			float capital = information.getloan_requested();
			int nbr_month = information.getDuration()*12; // duration in month
			int nbr_periodicity = information.getPeriodicity(); 
			float rate = information.getFix_Rate();
			float insuranceRate = information.getinsurance();
			//***********************************Compute the interest by the degressive method
			System.out.println("************************************************");
			System.out.println("********Compute the interest by the degressive method********");
			InterestDegressiveCounting iCC = new InterestDegressiveCounting(capital, nbr_periodicity, nbr_month , rate, insuranceRate );
			float sum = 0;
			for (int i =1;i<=nbr_periodicity;i++)
			{
				double mentialite = iCC.CalculMent(i);
				sum+=mentialite;
				System.out.println("La mentualite du mois N"+i+" est "+mentialite+" €, le taux d'interet est: " + iCC.getInteret() + " %, le capital restant est : "+iCC.getCapitalRestantDu()+" €");
			}
			System.out.println("La somme totale à rendre: "+sum+" €");

			break;
		}

		default:
		{
			System.out.println("Retapez votre choix, svp!! Le choi doit etre entre 1 et 2");
			loanCalculation();
			break;
		}
		}
	}
}
