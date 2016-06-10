package lendingscenario;

public class TraitmentScenario {

	private float capital;
	private int delay;
	
	public float insurance(float amount, int delay)
	{

		float valueinssurance;
		delay = delay*12;
		valueinssurance=amount/(delay);
		return valueinssurance;
	}
	
	public float updateCRD(float capital, float lastCRD)
	{
		float crd=0;
		crd=capital-lastCRD;
		
		return crd;
	}
	
	public float interest(float rate, int frequency)
	{
		//System.out.println("------------Interêt----------");
		int duration = delay * 12;
		float crd;
		float frequencyinterest = 0;
		float firstCRD=0;
		float insurance=0;
		for(int i=1; i<=duration; i++)
		{
			insurance = this.insurance(100, this.delay);
			
			if(i==1){
				firstCRD = this.updateCRD(this.capital, 0);
				frequencyinterest=((firstCRD*rate*0.01f)/12)*frequency;
				
			//	System.out.println(i+"  => "+firstCRD +" : "+frequencyinterest+" : "+insurance);
				
			}
			else	{
				crd=this.updateCRD(this.capital, frequencyinterest);
				
				frequencyinterest=((crd*rate*0.01f)/12)*frequency;
				
			//	System.out.println(i+"  => "+crd +" : "+frequencyinterest+" : "+insurance);
			}
			
		}
		
		return frequencyinterest;
	}
	
	
	public float digressive(float capital)
	{
		System.out.println("------------Dégréssive----------");
		float principal=0;
		this.delay=delay*12;
		for(int i=1; i<=delay; i++){
		principal=capital/delay;
		System.out.println("Mois "+i+" : "+principal);
		}
		return principal;	
	}
	
	
	public  void constants(float capital, int rate, int frequency){
		
		float insurance,interest;
		this.delay=delay*12;
		float principal=0, monthlyInstallment;
		int variableRate=1+rate;
			double pow=	Math.pow(variableRate, -delay);
		//System.out.println("pow: "+ pow);
		insurance = this.insurance(100, this.delay);

		
		
		
		
		System.out.println("------------constant----------");
		for(int i=1; i<=delay; i++)
		{
			//capital=this.updateCRD(capital, lastCRD);
			monthlyInstallment=(float) ((capital*rate)/(1-pow));
			
			System.out.println(" montant mensuel constant:"+ monthlyInstallment);
			interest=this.interest(rate, frequency);	
		principal=monthlyInstallment;
		//System.out.println("Mois "+ i+" : "+principal);
		}
		
		//return principal;
	}
	
	

	public static void main(String[] args) {
		TraitmentScenario ts = new TraitmentScenario();
		ts.capital=18000;
		ts.delay=2;
		//ts.interest(5, 1);
//	ts.digressive(ts.capital);
		ts.constants(ts.capital, 5, 1);
		
		
		
	}
}
