package lendingscenario;

import java.util.Random;

public class FloatingRate {

	public static int Value_Rate=4;
	public static int Min=1;
	public static int Max=10;
	
	
	
	//This method generates the rate between 2-9 percent. Each year the rate varies , it can grow as decrease. .
	public static int Rate(){
		int  BoardRate []={-2,-1,0,1,2};
		Random r=new Random();
		int index=r.nextInt(5);
		int number=BoardRate[index];
		
		number+=Value_Rate;
		
		if(number>Min && number<Max)
		return number;
		else 
		return Value_Rate;
		
	}
	
	public static void main(String[] args) {
		
		FloatingRate fr=new FloatingRate();
		System.out.println(fr.Rate());
	}
	
}
