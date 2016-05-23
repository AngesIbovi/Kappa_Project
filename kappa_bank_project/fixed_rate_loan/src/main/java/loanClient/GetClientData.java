package loanClient;

import java.util.Scanner;

public class GetClientData  {

	public static ClientData Insert() {
		ClientData information;

		Scanner sc = new Scanner(System.in);
		System.out.println("Le montant de frais de dossier est fix� � :"+ClientData.application_fee);
		System.out.println("Donnez le montant du pr�t: ");
		float amount_orrowed = sc.nextFloat();
		System.out.println("Donnez la dur�e du pr�t par an: ");
		int duration = sc.nextInt();
		System.out.println("Donnez le nombre de mensualit� du pr�t: ");
		int periodicity = sc.nextInt();
		System.out.println("Donnez le pourcentage de l'assurance: ");
		float value_insurance = sc.nextFloat();
		// add the fix rate
		System.out.println("Donnez le taux fixe: ");
		float fix_rate = sc.nextFloat();
		
		 information = new ClientData(amount_orrowed, duration, periodicity, value_insurance, fix_rate);
		
		System.out.println(information.toString());
		
		return information;
	
	}
	
}