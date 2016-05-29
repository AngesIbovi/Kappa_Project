import java.util.StringTokenizer;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testeur {
	
	public boolean stringTesteur(String mot){
		boolean response=false;
		
	
Pattern pattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
Matcher matcher = pattern.matcher(mot);
boolean test = matcher.find();

if (test)response=true;

		
		return response;
	}
	
	
	public boolean intTesteur(String mot){
		boolean response=false;
		
		
		Pattern pattern = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(mot);
		boolean test = matcher.find();

		if (test)response=true;

				
				return response;

		
	}
	
	
	public static void main(String[] args) {
		String mot1 = "Anges<>£££££££££££££%/%";
		String mot2 ="anges23";
		String mot3 ="anges";
		String mot4 ="1234";
		
		Testeur testeur = new Testeur();
		
	 System.out.println(testeur.stringTesteur(mot1));
	 System.out.println(testeur.stringTesteur(mot2));
	 System.out.println(testeur.stringTesteur(mot3));
	 System.out.println(testeur.stringTesteur(mot4));
	 
	 System.out.println(testeur.intTesteur(mot1));
	 System.out.println(testeur.intTesteur(mot2));
	 System.out.println(testeur.intTesteur(mot3));
	 System.out.println(testeur.intTesteur(mot4));
		
		
	}

}
