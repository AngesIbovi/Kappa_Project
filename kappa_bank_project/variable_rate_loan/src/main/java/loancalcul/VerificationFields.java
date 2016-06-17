package loancalcul;

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class VerificationFields {
	
	
	
	public static void veriftypeloans(float amount, String typeofloan){
		
		if(typeofloan=="Prêt étudiant" && amount>50000)
		{
			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(950,950));
			JOptionPane.showMessageDialog(frame, "Le montant inséré est de "+amount+"euro, le montant d'un prêt étudiant doit être<50000 euro");

	}else if(typeofloan=="Prêt immobilier" && amount>50000)
	{
		
		JFrame frame=new JFrame("JOptionPane showMessageDialog");
		frame.setSize(new Dimension(950,950));
		JOptionPane.showMessageDialog(frame, "Le montant inséré est de "+amount+"euro, le montant d'un prêt immobilier doit être > 50000 euro");

	}
	else if(typeofloan=="Prêt conso" && amount<25000){
		JFrame frame=new JFrame("JOptionPane showMessageDialog");
		frame.setSize(new Dimension(950,950));
		JOptionPane.showMessageDialog(frame, "Le montant inséré est de "+amount +"euro, le montant d'un prêt conso doit être<25000 euro");

	}
	}
	
	public void verifmaxduration(String typeofloan, int delay)
	{
		if(typeofloan=="Prêt étudiant" && delay >9)
		{
			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(950,950));
			JOptionPane.showMessageDialog(frame, "La durée du prêt étudiant est de 9 ans max");
		
		}
		else if(typeofloan=="Prêt immobilier" && delay >25){
			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(950,950));
			JOptionPane.showMessageDialog(frame, "La durée du prêt est au plus de 25 ans");
		}
		else if(typeofloan=="Prêt immobilier" && delay >2)
		{
			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(950,950));
			JOptionPane.showMessageDialog(frame, "La durée du prêt est au plus de 2 ans");
		
		}
		
	}
		public void verifdelay(int delay){
		if(delay < 1){
			
			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(800,800));
			JOptionPane.showMessageDialog(frame, "La durée ne peut être inférieur à une année");
		}
		
	}
		
		
		public boolean stringTesteur(String mot){
			boolean response=false;
			
		
	Pattern pattern = Pattern.compile("[^a-z][A-Z]", Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(mot);
	boolean test = matcher.find();

	if (test){
	JFrame frame=new JFrame("JOptionPane showMessageDialog");
	frame.setSize(new Dimension(800,800));
	JOptionPane.showMessageDialog(frame,mot + " Invalide!! Veuillez mettre un caratère convenable");
	}
			
			return response;
		}
		
		
		public boolean intTesteur(String mot){
			boolean response=false;
			
			
			Pattern pattern = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(mot);
			boolean test = matcher.find();

			if (test){

			JFrame frame=new JFrame("JOptionPane showMessageDialog");
			frame.setSize(new Dimension(800,800));
			JOptionPane.showMessageDialog(frame,mot + " Invalide!! veillez mettre des chiffres");
		
			}
					return response;

		}
		

}
	
