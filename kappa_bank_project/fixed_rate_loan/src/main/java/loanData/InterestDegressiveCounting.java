package loanData;

public class InterestDegressiveCounting {

	//initial data
	float montant;
	int nbEcheances;
	int nbEcheancesParAn;
	float tauxInteretAnnuel;
	float tauxAssurance; //Attention aux valeurs des taux. Par exemple 0.35% = 0.0035 !

	//Data to calculate
	float tauxPeriodique;
	float echeance;

	//Data handling
	float capitalRestantDu;
	int echeanceActuelle;
	float interet;
	float assurance;
	float principal;


	//Manufacturer (calculating periodic rate and maturity)
	public InterestDegressiveCounting (float montant,int nbEcheances,int nbEcheancesParAn,float tauxInteretAnnuel, float tauxAssurance) {
		//Retrieving argument in past data
		this.montant = montant;
		this.nbEcheances=nbEcheances;
		this.nbEcheancesParAn=nbEcheancesParAn;
		this.tauxInteretAnnuel=(float) (tauxInteretAnnuel*0.01);
		this.tauxAssurance=(float) (tauxAssurance*0.01);

		//Initialization data to handle
		capitalRestantDu = montant;
		echeanceActuelle = 1;

		//Calculation of data computing
		tauxPeriodique = this.tauxInteretAnnuel / nbEcheances;
		echeance = (float) ((montant * tauxPeriodique) / (1 - Math.pow(1 + tauxPeriodique, -nbEcheances)));
		
		//Calculation of the monthly payment and its three components


		assurance = montant * this.tauxAssurance / nbEcheances;
	}
	public float CalculMent (int mois ){
		interet = capitalRestantDu * tauxPeriodique;
		if(mois == nbEcheances) {
			//Last term, a little more expensive than others (no more than one or two � hopefully)
			principal = capitalRestantDu;
		} else {
			principal = echeance + (assurance + interet);
		}

		capitalRestantDu =Math.abs(capitalRestantDu-principal);
		return principal;
	}

//************************Set and gets of all attributes	
	public float getMontant() {
		return montant;
	}
	public void setMontant(float montant) {
		this.montant = montant;
	}
	public int getNbEcheances() {
		return nbEcheances;
	}
	public void setNbEcheances(int nbEcheances) {
		this.nbEcheances = nbEcheances;
	}
	public int getNbEcheancesParAn() {
		return nbEcheancesParAn;
	}
	public void setNbEcheancesParAn(int nbEcheancesParAn) {
		this.nbEcheancesParAn = nbEcheancesParAn;
	}
	public float getTauxInteretAnnuel() {
		return tauxInteretAnnuel;
	}
	public void setTauxInteretAnnuel(float tauxInteretAnnuel) {
		this.tauxInteretAnnuel = tauxInteretAnnuel;
	}
	public float getTauxAssurance() {
		return tauxAssurance;
	}
	public void setTauxAssurance(float tauxAssurance) {
		this.tauxAssurance = tauxAssurance;
	}
	public float getTauxPeriodique() {
		return tauxPeriodique;
	}
	public void setTauxPeriodique(float tauxPeriodique) {
		this.tauxPeriodique = tauxPeriodique;
	}
	public float getEcheance() {
		return echeance;
	}
	public void setEcheance(float echeance) {
		this.echeance = echeance;
	}
	public float getCapitalRestantDu() {
		return capitalRestantDu;
	}
	public void setCapitalRestantDu(float capitalRestantDu) {
		this.capitalRestantDu = capitalRestantDu;
	}
	public int getEcheanceActuelle() {
		return echeanceActuelle;
	}
	public void setEcheanceActuelle(int echeanceActuelle) {
		this.echeanceActuelle = echeanceActuelle;
	}
	public float getInteret() {
		return interet;
	}
	public void setInteret(float interet) {
		this.interet = interet;
	}
	public float getAssurance() {
		return assurance;
	}
	public void setAssurance(float assurance) {
		this.assurance = assurance;
	}
	public float getPrincipal() {
		return principal;
	}
	public void setPrincipal(float principal) {
		this.principal = principal;
	}



}
