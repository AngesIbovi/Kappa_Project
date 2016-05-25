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
	
	public float interest(int rate, int frequency)
	{
		
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
				
				System.out.println(i+"  => "+firstCRD +" : "+frequencyinterest+" : "+insurance);
				
			}
			else	{
				crd=this.updateCRD(this.capital, frequencyinterest);
				
				frequencyinterest=((crd*rate*0.01f)/12)*frequency;
				
				System.out.println(i+"  => "+crd +" : "+frequencyinterest+" : "+insurance);
			}
			
		}
		
		
		
		return frequencyinterest;
	}
	
	
	public float degressive(float capital)
	{
		float amountDEG;
		this.delay=delay*12;
		
		amountDEG=capital/delay;
		
		return amountDEG;
	}
	
	
	
//	public float constant(float capital,)
//	{
//		float amountDEG;
//		this.delay=delay*12;
//		
//		amountDEG=capital/delay;
//		
//		return amountDEG;
//	}
	public static void main(String[] args) {
		TraitmentScenario ts = new TraitmentScenario();
		ts.capital=18000;
		ts.delay=2;
		ts.interest(5, 1);
		
	}
}
