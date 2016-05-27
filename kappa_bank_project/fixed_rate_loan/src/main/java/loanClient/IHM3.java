package loanClient;

import javax.swing.JPanel;

import model.SessionInformation;
import model.query.ClientQuery;
import model.query.GetAccountsQuery;
import model.query.GetSimsQuery;
import model.response.GetSimsServerResponse;
import model.response.ServerResponse;
import model.response.GetAccountsServerResponseMO.Account;
import model.response.GetAccountsServerResponseMO;
import model.response.GetSimsServerResponse.SimulationIdentifier;
import util.JsonImpl;
import util.KappaProperties;
import view.Tab;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import loanData.InsuranceCounting;
import loanData.InterestCounting;
import loanData.InterestDegressiveCounting;
import loanData.LoanCounting;

import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import java.awt.Font;

public class IHM3 extends Tab {
	private JTextField textField;
	private Socket socket;
	private final IHM3 thisObject = this;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	public Account accountSelected;
	public JLabel lblNewLabel_1;


	/**
	 * Create the panel.
	 */
	public IHM3() {
		super("Simuler un prêt à taux fixe", 1);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 74, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblHelloWorl = new JLabel("Simuler un pret à taux fixe");
		lblHelloWorl.setFont(new Font("Tahoma", Font.BOLD, 24));
		GridBagConstraints gbc_lblHelloWorld = new GridBagConstraints();
		gbc_lblHelloWorld.gridwidth = 4;
		gbc_lblHelloWorld.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblHelloWorld.insets = new Insets(0, 0, 5, 5);
		gbc_lblHelloWorld.gridx = 0;
		gbc_lblHelloWorld.gridy = 0;
		add(lblHelloWorl, gbc_lblHelloWorld);
		
		lblNewLabel_1 = new JLabel("");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.gridwidth = 6;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 4;
		gbc_lblNewLabel_1.gridy = 0;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		JLabel lblMethodeDuCalcul = new JLabel("Methode du calcul");
		GridBagConstraints gbc_lblMethodeDuCalcul = new GridBagConstraints();
		gbc_lblMethodeDuCalcul.anchor = GridBagConstraints.WEST;
		gbc_lblMethodeDuCalcul.insets = new Insets(0, 0, 5, 5);
		gbc_lblMethodeDuCalcul.gridx = 0;
		gbc_lblMethodeDuCalcul.gridy = 1;
		add(lblMethodeDuCalcul, gbc_lblMethodeDuCalcul);

		final JComboBox comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 3;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		add(comboBox, gbc_comboBox);
		comboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Méthode Constante", "Méthode Dégressive",  " ", " " }));
		
				JLabel lblNewLabel = new JLabel("Résultat");
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.gridwidth = 2;
				gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel.gridx = 7;
				gbc_lblNewLabel.gridy = 1;
				add(lblNewLabel, gbc_lblNewLabel);


		JLabel lblTypeDePret = new JLabel("Type de pret");
		GridBagConstraints gbc_lblTypeDePret = new GridBagConstraints();
		gbc_lblTypeDePret.anchor = GridBagConstraints.WEST;
		gbc_lblTypeDePret.insets = new Insets(0, 0, 5, 5);
		gbc_lblTypeDePret.gridx = 0;
		gbc_lblTypeDePret.gridy = 2;
		add(lblTypeDePret, gbc_lblTypeDePret);

		JComboBox comboBox_1 = new JComboBox();
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.gridwidth = 3;
		gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 2;
		add(comboBox_1, gbc_comboBox_1);
		comboBox_1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Prêt Personnel", "Prêt consommation", "Prêt immobilier", "Prêt etudiant", " ", " " }));


		JLabel lblMontantDuPret = new JLabel("Montant du pret");
		GridBagConstraints gbc_lblMontantDuPret = new GridBagConstraints();
		gbc_lblMontantDuPret.fill = GridBagConstraints.VERTICAL;
		gbc_lblMontantDuPret.insets = new Insets(0, 0, 5, 5);
		gbc_lblMontantDuPret.anchor = GridBagConstraints.WEST;
		gbc_lblMontantDuPret.gridx = 0;
		gbc_lblMontantDuPret.gridy = 3;
		add(lblMontantDuPret, gbc_lblMontantDuPret);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 3;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 3;
		add(textField, gbc_textField);
		textField.setColumns(10);

		JLabel lblDureDuPret = new JLabel("Durée du pret");
		GridBagConstraints gbc_lblDureDuPret = new GridBagConstraints();
		gbc_lblDureDuPret.anchor = GridBagConstraints.WEST;
		gbc_lblDureDuPret.insets = new Insets(0, 0, 5, 5);
		gbc_lblDureDuPret.gridx = 0;
		gbc_lblDureDuPret.gridy = 4;
		add(lblDureDuPret, gbc_lblDureDuPret);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 3;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 4;
		add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		final JTextPane textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridheight = 5;
		gbc_textPane.gridwidth = 5;
		gbc_textPane.insets = new Insets(0, 0, 5, 0);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 5;
		gbc_textPane.gridy = 2;
		add(textPane, gbc_textPane);

		JLabel lblNombreDeMensualit = new JLabel("Nombre de mensualité");
		GridBagConstraints gbc_lblNombreDeMensualit = new GridBagConstraints();
		gbc_lblNombreDeMensualit.anchor = GridBagConstraints.WEST;
		gbc_lblNombreDeMensualit.insets = new Insets(0, 0, 5, 5);
		gbc_lblNombreDeMensualit.gridx = 0;
		gbc_lblNombreDeMensualit.gridy = 5;
		add(lblNombreDeMensualit, gbc_lblNombreDeMensualit);

		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.gridwidth = 3;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 5;
		add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);

		JLabel lblInteret = new JLabel("Interet");
		GridBagConstraints gbc_lblInteret = new GridBagConstraints();
		gbc_lblInteret.anchor = GridBagConstraints.WEST;
		gbc_lblInteret.insets = new Insets(0, 0, 5, 5);
		gbc_lblInteret.gridx = 0;
		gbc_lblInteret.gridy = 6;
		add(lblInteret, gbc_lblInteret);

		textField_3 = new JTextField();
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.gridwidth = 3;
		gbc_textField_3.insets = new Insets(0, 0, 5, 5);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 6;
		add(textField_3, gbc_textField_3);
		textField_3.setColumns(10);

		JLabel lblAssurance = new JLabel("Assurance");
		GridBagConstraints gbc_lblAssurance = new GridBagConstraints();
		gbc_lblAssurance.anchor = GridBagConstraints.WEST;
		gbc_lblAssurance.insets = new Insets(0, 0, 0, 5);
		gbc_lblAssurance.gridx = 0;
		gbc_lblAssurance.gridy = 7;
		add(lblAssurance, gbc_lblAssurance);

		textField_4 = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.gridwidth = 3;
		gbc_textField_4.insets = new Insets(0, 0, 0, 5);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 7;
		add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);


		//Button
		final JButton btnSimuler = new JButton("Simuler");
		btnSimuler.setEnabled(true);
		btnSimuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				float capital = Float.parseFloat(textField.getText());
				int nbr_month = Integer.parseInt(textField_1.getText());
				int nbr_periodicity = Integer.parseInt(textField_2.getText()); 
				float rate = Float.parseFloat(textField_3.getText());
				float insuranceRate = Float.parseFloat(textField_4.getText());
				int methodeSelected = comboBox.getSelectedIndex();
				System.out.println("capital: "+capital);
				System.out.println("nbr_month: "+nbr_month);
				System.out.println("nbr_periodicity: "+nbr_periodicity);
				System.out.println("rate: "+rate);
				System.out.println("insuranceRate: "+insuranceRate);
				System.out.println("methodeSelected: "+methodeSelected);
				System.out.println("accountSelected: "+accountSelected.getAccount_id()); 				
				textPane.setText("");

				switch (methodeSelected) {
				case 0://Degressive
				{
					LoanCounting loanCounting = new LoanCounting();
					InterestCounting interestCounting= new InterestCounting();
					InsuranceCounting insuranceCounting= new InsuranceCounting();

					float paymentPerMonth = loanCounting.paymentPerMonth(capital, nbr_periodicity);
					float interestPerMonth = interestCounting.interestPerMonth(capital, rate, nbr_periodicity);
					float insurancePerMonth = insuranceCounting.insurancePerMonth(capital, insuranceRate, nbr_periodicity);
					float totalPaymentPerMonth = paymentPerMonth + interestPerMonth + insurancePerMonth;

					//			System.out.println("Le montant du cr�dit initial � payer par mois: "+paymentPerMonth);
					//			System.out.println("Le montant d'interet � payer par mois: "+interestPerMonth);
					//			System.out.println("Le frais d'assurance � payer par mois: "+insurancePerMonth);
					System.out.println("****************");
					String messageToDisplay = "";
					messageToDisplay += ("********Compute the interest by the constant method********");
					float total = totalPaymentPerMonth * nbr_periodicity;
					float totalToPay = total;
					for (int i =1;i<=nbr_periodicity;i++)
					{
						total-=totalPaymentPerMonth;
						messageToDisplay += ("\n\nLa mentualite du mois N°"+i+" est "+totalPaymentPerMonth+" €, le taux d'interret est: "+rate+" %, le capital restant est : " +total+" €");
					}
					messageToDisplay += ("\n\n\nLa somme totale à rendre: "+totalToPay+" €");
					textPane.setText(messageToDisplay);
					messageToDisplay = "";
					break;
				}
				case 1: 
				{
					String messageToDisplay = "";
//					messageToDisplay += ("Calcul du pret avec la methode Degressive");
					//				ClientData information = GetClientData.Insert();
					//				float capital = information.getloan_requested();
					//				int nbr_month = information.getDuration()*12; // duration in month
					//				int nbr_periodicity = information.getPeriodicity(); 
					//				float rate = information.getFix_Rate();
					//				float insuranceRate = information.getinsurance();
					//***********************************Compute the interest by the degressive method
					System.out.println("************************************************");
					messageToDisplay += ("********Compute the interest by the degressive method********");
					InterestDegressiveCounting iCC = new InterestDegressiveCounting(capital, nbr_periodicity, nbr_month , rate, insuranceRate );
					float sum = 0;
					for (int i =1;i<=nbr_periodicity;i++)
					{
						double mentialite = iCC.CalculMent(i);
						sum+=mentialite;
						messageToDisplay += ("\n\nLa mentualite du mois N°"+i+" est "+mentialite+" €, le taux d'interet est: " + iCC.getInteret() + " %, le capital restant est : "+iCC.getCapitalRestantDu()+" €");
					}
					messageToDisplay += ("\n\n\nLa somme totale à rendre: "+sum+" €");
					textPane.setText(messageToDisplay);
					messageToDisplay = "";
					break;
				}

				default:
				{
					System.out.println("Retapez votre choix, svp!! Le choi doit etre entre 1 et 2");
					break;
				}
				}


			}
		});
		GridBagConstraints gbc_btnAlle = new GridBagConstraints();
		gbc_btnAlle.insets = new Insets(0, 0, 0, 5);
		gbc_btnAlle.gridx = 7;
		gbc_btnAlle.gridy = 7;
		add(btnSimuler, gbc_btnAlle);



		//Tout ce qui n'a pas besoin de session information

	}

	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		this.socket = sessionInformation.getSocket();
	}

}
