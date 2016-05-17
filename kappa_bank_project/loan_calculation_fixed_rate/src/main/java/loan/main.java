package loan;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CalculPretTauxFixe a = new CalculPretTauxFixe(10000, 20, 5, 0.02, 0.0035);
		
		for (int i =1;i<=20;i++)
		{
			double mentialite = a.CalculMent(i);
			System.out.println("mentialite du moi n "+i+" est "+mentialite+" le capital restant est : "+a.capitalRestantDu);
		}
		
	}

}
