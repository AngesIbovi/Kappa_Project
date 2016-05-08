package loan;

public class CalculPretTauxFixe {
	
	//initial data
		double montant;
		int nbEcheances;
		int nbEcheancesParAn;
		double tauxInteretAnnuel;
		double tauxAssurance; //Attention aux valeurs des taux. Par exemple 0.35% = 0.0035 !
		
		//Data to calculate
		double tauxPeriodique;
		double echeance;
		
		//Data handling
		double capitalRestantDu;
		int echeanceActuelle;
		double interet;
		double assurance;
		double principal;
				
		
		//Manufacturer (calculating periodic rate and maturity)
		public CalculPretTauxFixe (double montant,int nbEcheances,int nbEcheancesParAn,double tauxInteretAnnuel, double tauxAssurance) {
			//Retrieving argument in past data
			this.montant = montant;
			this.nbEcheances=nbEcheances;
			this.nbEcheancesParAn=nbEcheancesParAn;
			this.tauxInteretAnnuel=tauxInteretAnnuel;
			this.tauxAssurance=tauxAssurance;
			
			//Initialization data to handle
			capitalRestantDu = montant;
			echeanceActuelle = 1;
			
			//Calculation of data computing
			tauxPeriodique = tauxInteretAnnuel / nbEcheancesParAn;
			echeance = (montant * tauxPeriodique) / (1 - Math.pow(1 + tauxPeriodique, -nbEcheances));
		
		
		//Calculation of the monthly payment and its three components
			
			
			assurance = montant * tauxAssurance / nbEcheances;
		}
			public double CalculMent (int mois ){
			interet = capitalRestantDu * tauxPeriodique;
			
			if(mois == nbEcheances) {
				//Last term, a little more expensive than others (no more than one or two � hopefully)
				principal = capitalRestantDu;
			} else {
				principal = echeance - (assurance + interet);
			}
			
			capitalRestantDu = capitalRestantDu-principal;
			return principal;
		}
		


}
