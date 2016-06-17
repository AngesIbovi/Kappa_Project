package org.interest_rate;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import serverCommunication.ServerCommunication;

import javax.swing.DefaultComboBoxModel;

public class risk_level implements ActionListener  {



	public JFrame frmNiveauDeRisque;
	public JTextField MontantTextFeild;
	public JTextField GuarantieText;
	public JTextField ApportPersoText;
	public JTextField DureeEmprText;
	static float rate;
	static float rate1; 
	static float rate2; 
	static float rate3; 
	static float ratePretEtud;

	public JRadioButton Radio1;
	public JLabel MontantLabel;
	public JLabel DureeLabel ;
	public JLabel GuarantieLabel;
	public JRadioButton Radio2 ;
	public JRadioButton Radio3;
	public JRadioButton Radio4;
	public JLabel rateLabel; 
	public JTextField rateText;
	private JTextField finalRateText;
	private JLabel label_3;
	private JButton btnNewButton;
	private JTextField RefRateText;
	private JComboBox comboBox;




	/**
	 * Create the application.
	 */
	public risk_level() {
		initialize();
	}

	/**
	 * Initialize the contents of sthe frame.
	 */
	private void initialize() {
		frmNiveauDeRisque = new JFrame();
		frmNiveauDeRisque.setTitle("Niveau de risque");
		frmNiveauDeRisque.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 11));
		frmNiveauDeRisque.getContentPane().setForeground(Color.BLACK);
		frmNiveauDeRisque.setBounds(300, 300, 900, 600);
		frmNiveauDeRisque.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNiveauDeRisque.getContentPane().setLayout(null);
		frmNiveauDeRisque.setResizable(false);

		MontantLabel = new JLabel("Montant d'emprunt");
		MontantLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		MontantLabel.setBounds(10, 177, 158, 20);
		frmNiveauDeRisque.getContentPane().add(MontantLabel);

		DureeLabel = new JLabel("Age de l'emprunteur");
		DureeLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		DureeLabel.setBounds(10, 242, 158, 19);
		frmNiveauDeRisque.getContentPane().add(DureeLabel);

		GuarantieLabel= new JLabel("Garanties de remboursement");
		GuarantieLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		GuarantieLabel.setBounds(10, 468, 226, 19);
		frmNiveauDeRisque.getContentPane().add(GuarantieLabel);

		MontantTextFeild = new JTextField();

		MontantTextFeild.setFont(new Font("Tahoma", Font.PLAIN, 15));
		MontantTextFeild.setBounds(308, 176, 114, 23);
		frmNiveauDeRisque.getContentPane().add(MontantTextFeild);
		MontantTextFeild.setColumns(10);
		MontantTextFeild.addActionListener(this);



		Radio1 = new JRadioButton("18 à 30 ans");
		Radio1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio1.setBounds(201, 240, 109, 23);
		Radio1.addActionListener(this);
		frmNiveauDeRisque.getContentPane().add(Radio1);

		Radio2 = new JRadioButton("De 30 à 45");
		Radio2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio2.setBounds(329, 240, 114, 23);
		Radio2.addActionListener(this);
		frmNiveauDeRisque.getContentPane().add(Radio2);

		Radio3 = new JRadioButton("De 45 à 60");
		Radio3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio3.setBounds(471, 240, 120, 23);
		Radio3.addActionListener((ActionListener) this);

		frmNiveauDeRisque.getContentPane().add(Radio3);

		Radio4 = new JRadioButton("60 et plus");
		Radio4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio4.setBounds(628, 240, 109, 23);
		Radio4.addActionListener(this);
		frmNiveauDeRisque.getContentPane().add(Radio4);




		ButtonGroup group = new ButtonGroup(); 
		group.add(Radio1);
		group.add(Radio2);
		group.add(Radio3);
		group.add(Radio4);




		GuarantieText = new JTextField();

		GuarantieText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GuarantieText.setHorizontalAlignment(SwingConstants.LEFT);
		GuarantieText.setBounds(308, 466, 114, 23);
		GuarantieText.setEditable(false);
		frmNiveauDeRisque.getContentPane().add(GuarantieText);
		GuarantieText.setColumns(10);

		JLabel ApportPersLab = new JLabel("Apport personnel");
		ApportPersLab.setFont(new Font("Tahoma", Font.BOLD, 15));
		ApportPersLab.setBounds(10, 311, 158, 20);
		frmNiveauDeRisque.getContentPane().add(ApportPersLab);

		ApportPersoText = new JTextField();


		ApportPersoText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		ApportPersoText.setBounds(308, 310, 114, 23);
		frmNiveauDeRisque.getContentPane().add(ApportPersoText);
		ApportPersoText.setColumns(10);

		JLabel DureeEmprlabel = new JLabel("Durée de l'emprunt");
		DureeEmprlabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		DureeEmprlabel.setBounds(10, 391, 158, 20);
		frmNiveauDeRisque.getContentPane().add(DureeEmprlabel);

		DureeEmprText = new JTextField();
		DureeEmprText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		DureeEmprText.setBounds(308, 390, 114, 22);
		frmNiveauDeRisque.getContentPane().add(DureeEmprText);
		DureeEmprText.setColumns(10);

		JLabel label_1 = new JLabel("(€)");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_1.setBounds(424, 182, 24, 14);
		frmNiveauDeRisque.getContentPane().add(label_1);

		JLabel label_2 = new JLabel(("(€)"));
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_2.setBounds(424, 316, 24, 14);
		frmNiveauDeRisque.getContentPane().add(label_2);

		JLabel lblAns = new JLabel("(ans)");
		lblAns.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblAns.setBounds(424, 396, 46, 14);
		frmNiveauDeRisque.getContentPane().add(lblAns);

		JLabel label = new JLabel("(€)");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label.setBounds(424, 472, 24, 14);
		frmNiveauDeRisque.getContentPane().add(label);






		rateLabel = new JLabel("Le Taux :");
		rateLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		rateLabel.setBounds(10, 529, 158, 19);
		frmNiveauDeRisque.getContentPane().add(rateLabel);

		finalRateText = new JTextField();
		finalRateText.setForeground(Color.RED);
		finalRateText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		finalRateText.setBounds(308, 529, 114, 23);
		frmNiveauDeRisque.getContentPane().add(finalRateText);
		finalRateText.setColumns(10);

		label_3 = new JLabel("(%)");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_3.setBounds(424, 533, 46, 14);
		frmNiveauDeRisque.getContentPane().add(label_3);





		btnNewButton = new JButton("Valider");
		btnNewButton.setBounds(639, 463, 87, 33);
		frmNiveauDeRisque.getContentPane().add(btnNewButton);










		Label label_5 = new Label("Types du prêt");
		label_5.setFont(new Font("Tahoma", Font.BOLD, 15));
		label_5.setBounds(10, 36, 130, 20);
		frmNiveauDeRisque.getContentPane().add(label_5);

		JLabel label_6 = new JLabel("Taux Indicateur");
		label_6.setFont(new Font("Tahoma", Font.BOLD, 15));
		label_6.setBounds(10, 105, 158, 20);
		frmNiveauDeRisque.getContentPane().add(label_6);

		RefRateText = new JTextField();
		RefRateText.setEditable(false);
		RefRateText.setColumns(10);
		RefRateText.setBounds(308, 107, 114, 20);
		frmNiveauDeRisque.getContentPane().add(RefRateText);

		JLabel label_4 = new JLabel("(%)");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_4.setBounds(424, 110, 46, 14);
		frmNiveauDeRisque.getContentPane().add(label_4);
		
		String [] title  = { "Prêt étudiant" , "Prêt immobilier" , "Prêt conso" };
		comboBox = new JComboBox(title);
		comboBox.setBounds(307, 36, 115, 20);
		comboBox.addActionListener(this);
		frmNiveauDeRisque.getContentPane().add(comboBox);
		

		
		btnNewButton.addActionListener(this);

	}












	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					risk_level window = new risk_level();
					window.frmNiveauDeRisque.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});




	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(comboBox.getSelectedIndex()==0){
		
			
			float ratePretEtud=9;
			
			String ratePretEtudText=Float.toString(ratePretEtud);
		
			RefRateText.setText(ratePretEtudText);
			
		}else if (comboBox.getSelectedIndex()==1){
			
               float ratePretEtud=25;
			
			String ratePretEtudText=Float.toString(ratePretEtud);
		
			RefRateText.setText(ratePretEtudText);
			
		}else if (comboBox.getSelectedIndex()==2){
			
            ratePretEtud=2;
			
			String ratePretEtudText=Float.toString(ratePretEtud);
		
			RefRateText.setText(ratePretEtudText);
			
		}
			
		
		if(e.getSource()==Radio1){
			rate=0;
			rate=(float) (rate + 0.5);
			System.out.println(rate);

			String rateString = Float.toString(rate);
			finalRateText.setText(rateString);


		}else if (e.getSource()==Radio2) {
			rate1=0;
			rate1=(float) (rate1 + 1.28);
			System.out.println(rate1);

			String rateString = Float.toString(rate1);
			finalRateText.setText(rateString);

		}else if (e.getSource()==Radio3) {
			rate2=0;
			rate2=(float) (rate2 + 1.98);
			System.out.println(rate2);

			String rateString = Float.toString(rate2);
			finalRateText.setText(rateString);

		}
		else if (e.getSource()==Radio4) {
			rate3=0;
			rate3=(float) (rate3 + 2.4);
			System.out.println(rate3);

			String rateString = Float.toString(rate3);
			finalRateText.setText(rateString);

		}


		if(e.getSource()==btnNewButton){

			//

			String amountString = MontantTextFeild.getText();
			float famount = Float.parseFloat(amountString);
			System.out.println("Le montant à emprunter :" + amountString);

			//
			String ApoPer = ApportPersoText.getText();
			float fApoPer = Float.parseFloat(ApoPer);
			System.out.println("l'apport personnel est du" +ApoPer);



			//

			float cv = (famount-fApoPer); 
			System.out.println(cv);

			//

			String dura= DureeEmprText.getText();
			//float f3 = Float.parseFloat(dura);
			System.out.println("La dur�e est : "+ dura);


			float remb =(cv/Float.parseFloat(dura));
			String rembString =new String();
			rembString = Float.toString(remb);
			GuarantieText.setText(rembString);


			
			
			
			//float 

			float rate5;
			rate5= ((remb*100)/famount)+ratePretEtud;
			System.out.println(rate5);


	 
 

			finalRateText.setText(Float.toString(rate5)) ;

		}

	}
}
