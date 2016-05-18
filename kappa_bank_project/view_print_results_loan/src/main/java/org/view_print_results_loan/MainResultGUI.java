package org.view_print_results_loan;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import model.query.GetAllSimsQuery;
import model.query.GetSimQuery;
import model.query.GetSimsQuery; 
import model.response.AuthenticationServerResponse;
import model.response.GetAllSimsServerResponse;
import model.response.GetAllSimsServerResponse.SimulationIdentifier;
import model.response.GetSimServerResponse;
import model.response.GetSimServerResponse.AmortizationType;
import model.response.GetSimServerResponse.Repayment;
import model.response.GetSimsServerResponse;
import util.JsonImpl;
import util.KappaProperties;    
 
/**
 * A Main Jframe used for the displaying results.
 * @version R3 Sprint 3 - 06/05/2016
 * @Author Kappa-V 
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class MainResultGUI extends JFrame {
	private JTable tblPayment;
	private JTable tblRepay;
 
	
	public MainResultGUI() throws ClassNotFoundException, SQLException, NumberFormatException, UnknownHostException, IOException { 
		final MainResultGUI thisObject = this;
		// Initializing tools
				try {
					KappaProperties.init();
					JsonImpl.init();
				} catch(IOException e) {
					System.out.print("Exiting Client.");
					throw e;
				}
		/* Network connection */
		
		// Socket initialization
		Properties prop = KappaProperties.getInstance();
		//System.out.print(prop);
		final Socket connection = new Socket("localhost", Integer.parseInt(prop.getProperty("SERVER_PORT")));
		final PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		

		// Cleanup planning  
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// TODO: check if on successful login, when the auth window gets disposed, this listener is called
				addWindowStateListener(new WindowStateListener() { 
					@Override
					public void windowStateChanged(WindowEvent e) {
						// TODO Auto-generated method stub
						if(e.getNewState() == WindowEvent.WINDOW_CLOSED) {
							out.println("BYE");
							try {
								connection.close();
							} catch (IOException e1) {
								e1.printStackTrace(); // For debug purposes.
							}
						}
					}
				});
				
				
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		getContentPane().setLayout(null);
		

		/* Swing components */
		
		// JPanel dimensions and position
		final JPanel panel = new JPanel();
		panel.setBounds(0, 0, 742, 31);
		getContentPane().add(panel);
		
		JLabel lblChoixDuScenario = new JLabel("Sélectionner le scenario :");
		lblChoixDuScenario.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(lblChoixDuScenario);

		//final JComboBox<SimulationIdentifier> cbScenChoice = new JComboBox<SimulationIdentifier>();
		final JComboBox<SimulationIdentifier> cbScenChoice = new JComboBox<SimulationIdentifier>();
		cbScenChoice.setToolTipText("Veuillez choisir la simulation");
		cbScenChoice.setEditable(true);
		cbScenChoice.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(cbScenChoice);
		
		final JLabel lblParamtres = new JLabel("Paramètres");
		lblParamtres.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblParamtres.setBounds(10, 74, 87, 22);
		getContentPane().add(lblParamtres);
		
		final JLabel lblTypeDePrt = new JLabel("Type de prêt :");
		lblTypeDePrt.setBounds(27, 124, 87, 14);
		getContentPane().add(lblTypeDePrt);
		
		final JLabel lblTypeLoan = new JLabel("");
		lblTypeLoan.setBackground(SystemColor.activeCaption);
		lblTypeLoan.setBounds(118, 124, 155, 14);
		getContentPane().add(lblTypeLoan); 
		lblTypeLoan.setOpaque(true);
		
		final JLabel lblge = new JLabel("Âge :");
		lblge.setBounds(283, 96, 38, 14);
		getContentPane().add(lblge);
		
		final JLabel lblAge = new JLabel("");
		lblAge.setOpaque(true);
		lblAge.setBackground(SystemColor.activeCaption);
		lblAge.setBounds(315, 96, 55, 14);
		getContentPane().add(lblAge);
		
		final JLabel lblMensualits = new JLabel("Montant du prêt :");
		lblMensualits.setBounds(393, 96, 97, 14);
		getContentPane().add(lblMensualits);
		
		final JLabel lblCapital = new JLabel("");
		lblCapital.setOpaque(true);
		lblCapital.setBackground(SystemColor.activeCaption);
		lblCapital.setBounds(500, 96, 55, 14);
		getContentPane().add(lblCapital);
		
		final JLabel lblhorsMisAssurance = new JLabel("(hors mis assurance)");
		lblhorsMisAssurance.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblhorsMisAssurance.setBounds(562, 96, 136, 14);
		getContentPane().add(lblhorsMisAssurance);
		
		final JLabel lblNumCompte = new JLabel("Num. Compte :");
		lblNumCompte.setBounds(283, 124, 87, 14);
		getContentPane().add(lblNumCompte);
		
		final JLabel lblNumAccount = new JLabel("");
		lblNumAccount.setOpaque(true);
		lblNumAccount.setBackground(SystemColor.activeCaption);
		lblNumAccount.setBounds(380, 124, 155, 14);
		getContentPane().add(lblNumAccount);
		
		final JLabel lblClient = new JLabel("Client :");
		lblClient.setBounds(76, 96, 38, 14);
		getContentPane().add(lblClient);
		
		final JLabel lblCustomer = new JLabel("");
		lblCustomer.setOpaque(true);
		lblCustomer.setBackground(SystemColor.activeCaption);
		lblCustomer.setBounds(118, 96, 155, 14);
		getContentPane().add(lblCustomer);
		
		final JLabel lblTypeDeRemboursement = new JLabel("Type de remboursement :");
		lblTypeDeRemboursement.setBounds(551, 124, 152, 14);
		getContentPane().add(lblTypeDeRemboursement);
		
		final JLabel lblRepaymentConstant = new JLabel("Constant");
		lblRepaymentConstant.setOpaque(true);
		lblRepaymentConstant.setBackground(SystemColor.activeCaption);
		lblRepaymentConstant.setBounds(713, 124, 85, 14);
		getContentPane().add(lblRepaymentConstant);
		
		final JLabel lblRsultat = new JLabel("Résultats");
		lblRsultat.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblRsultat.setBounds(10, 149, 87, 22);
		getContentPane().add(lblRsultat);
		
		final JLabel lblTauxDintrt = new JLabel("Taux d'intérêt :");
		lblTauxDintrt.setBounds(20, 175, 94, 14);
		getContentPane().add(lblTauxDintrt);
		
		final JLabel lblLoanRate = new JLabel("");
		lblLoanRate.setOpaque(true);
		lblLoanRate.setBackground(SystemColor.activeCaption);
		lblLoanRate.setBounds(118, 175, 74, 14);
		getContentPane().add(lblLoanRate);
		
		final JLabel label = new JLabel("(hors mis assurance)");
		label.setFont(new Font("Tahoma", Font.ITALIC, 11));
		label.setBounds(198, 175, 136, 14);
		getContentPane().add(label);
		
		final JLabel lblMontantDesMensualits = new JLabel("Mensualités :");
		lblMontantDesMensualits.setBounds(30, 202, 84, 14);
		getContentPane().add(lblMontantDesMensualits);
		
		final JLabel lblRepaymentAmount = new JLabel("");
		lblRepaymentAmount.setOpaque(true);
		lblRepaymentAmount.setBackground(SystemColor.activeCaption);
		lblRepaymentAmount.setBounds(118, 200, 74, 14);
		getContentPane().add(lblRepaymentAmount);
		
		final JLabel lblTauxDassurance = new JLabel("Taux d'assurance :");
		lblTauxDassurance.setBounds(199, 200, 114, 14);
		getContentPane().add(lblTauxDassurance);
		
		final JLabel lblInsuranceRate = new JLabel("");
		lblInsuranceRate.setOpaque(true);
		lblInsuranceRate.setBackground(SystemColor.activeCaption);
		lblInsuranceRate.setBounds(315, 200, 74, 14);
		getContentPane().add(lblInsuranceRate);
		
		final JLabel lblTeg = new JLabel("TEG * :");
		lblTeg.setBounds(399, 202, 38, 14);
		getContentPane().add(lblTeg);
		
		final JLabel lblEffectiveRate = new JLabel("");
		lblEffectiveRate.setOpaque(true);
		lblEffectiveRate.setBackground(SystemColor.activeCaption);
		lblEffectiveRate.setBounds(436, 202, 74, 14);
		getContentPane().add(lblEffectiveRate);
		
		final JLabel lblCotTotalDu = new JLabel("Coût total du crédit *:");
		lblCotTotalDu.setBounds(21, 225, 136, 14);
		getContentPane().add(lblCotTotalDu);
		
		final JLabel lblTotalCreditCost = new JLabel("");
		lblTotalCreditCost.setOpaque(true);
		lblTotalCreditCost.setBackground(SystemColor.activeCaption);
		lblTotalCreditCost.setBounds(138, 225, 74, 14);
		getContentPane().add(lblTotalCreditCost);
		
		final JLabel lblIntrt = new JLabel("Intérêts :");
		lblIntrt.setBounds(224, 225, 63, 14);
		getContentPane().add(lblIntrt);
		
		final JLabel lblInterest = new JLabel("");
		lblInterest.setOpaque(true);
		lblInterest.setBackground(SystemColor.activeCaption);
		lblInterest.setBounds(283, 225, 74, 14);
		getContentPane().add(lblInterest);
		
		final JLabel lblAssurance = new JLabel("Assurance :");
		lblAssurance.setBounds(374, 225, 74, 14);
		getContentPane().add(lblAssurance);
		
		final JLabel lblInsurance = new JLabel("");
		lblInsurance.setOpaque(true);
		lblInsurance.setBackground(SystemColor.activeCaption);
		lblInsurance.setBounds(447, 225, 74, 14);
		getContentPane().add(lblInsurance);
		
		final JLabel lblFraisDeDossier = new JLabel("Frais de dossier :");
		lblFraisDeDossier.setBounds(554, 225, 97, 14);
		getContentPane().add(lblFraisDeDossier);
		
		final JLabel lblApplicationFee = new JLabel("");
		lblApplicationFee.setOpaque(true);
		lblApplicationFee.setBackground(SystemColor.activeCaption);
		lblApplicationFee.setBounds(653, 225, 74, 14);
		getContentPane().add(lblApplicationFee);
		
		//We define the home choice of ComboBox
		String lblChoix = "- Choisir -";
		cbScenChoice.setSelectedItem(lblChoix);  
		
		final JLabel lblTitle = new JLabel("");
		lblTitle.setForeground(Color.BLUE);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTitle.setBounds(167, 42, 473, 14);
		getContentPane().add(lblTitle);
		
		JLabel lblNomDuScenario = new JLabel("Nom du scenario :");
		lblNomDuScenario.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNomDuScenario.setBounds(10, 42, 147, 22);
		getContentPane().add(lblNomDuScenario);
		
		tblRepay = new JTable();
		tblRepay.setBounds(27, 272, 700, 422);
		getContentPane().add(tblRepay);
		
		JScrollPane scrollPane = new JScrollPane(tblRepay);
		scrollPane.setBounds(27, 272, 825, 422);
		getContentPane().add(scrollPane);
		
		JLabel lblTableauDamortissement = new JLabel("TABLEAU D'AMORTISSEMENT");
		lblTableauDamortissement.setForeground(Color.BLUE);
		lblTableauDamortissement.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTableauDamortissement.setBounds(269, 250, 473, 14);
		getContentPane().add(lblTableauDamortissement);
		
 
		
		// Sending the account_id over to the server
		GetAllSimsQuery query = new GetAllSimsQuery("-1");
		out.println(query.toString());
		// Receiving the server's response
		String message = in.readLine();
		//System.out.print(message);
		//Treating the server's response
		try {
			// Prefix and content detection
			int prefixEnd = message.indexOf(' ');
			
			if(prefixEnd == -1) {
				throw new Exception("No prefix");
			}
			
			String prefix = message.substring(0, prefixEnd);
			String content = message.substring(prefixEnd + 1);
			
			//System.out.print(message);
			// Prefix identification
			switch(prefix) {
			case "ERR":
				JOptionPane.showMessageDialog(thisObject, "Format error. Try downloading the newest version.");
				break;
			
			case "OK":
				// De-serialization
				GetAllSimsServerResponse response = JsonImpl.fromJson(content, GetAllSimsServerResponse.class);     
				System.out.println(response.getSimulations()); 
				List<SimulationIdentifier> listSims=  response.getSimulations();  
				for(int i=0; i<listSims.toArray().length;i++){
				System.out.println(listSims.toArray()[i]);
				cbScenChoice.addItem((SimulationIdentifier) listSims.toArray()[i]); 
			    } 
				//System.out.println(listSims.toArray().length); 
				 
 				//System.out.println(theList);
				//for(int i=0; i<=theList.length;i++){
					//System.out.println(theList[i]);
				//}
				//cbScenChoice.setModel(response);
				break;
			
			default:
				throw new Exception("Unknown prefix");
			}
		} catch (Exception e1) {
			//System.out.print(e1); 
			JOptionPane.showMessageDialog(thisObject, "Unknown response format. Please try again later or download the newest version.");
		} 
		
		//Action on select item in combox list of scenario
		cbScenChoice.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				EventQueue.invokeLater(new Runnable() { // Starting a thread is long, so we need to clear the eventqueue first
					public void run() {
						new Thread(new Runnable() { // We launch a new thread for this treatment, so that the GUI can still update. This new thread will be the host for the onSuccessfulLogin callable 
							public void run() {
							 
								try {
									// Sending the loan_id over to the server
									GetSimQuery query = new GetSimQuery(((SimulationIdentifier) cbScenChoice.getSelectedItem()).getId());
									out.println(query.toString());
									// Receiving the server's response
									String message = in.readLine(); 

									//Treating the server's response
									try {
										// Prefix and content detection
										int prefixEnd = message.indexOf(' ');
										
										if(prefixEnd == -1) {
											throw new Exception("No prefix");
										}
										
										String prefix = message.substring(0, prefixEnd);
										String content = message.substring(prefixEnd + 1);
										
										// Prefix identification
										switch(prefix) {
										case "ERR":
											JOptionPane.showMessageDialog(thisObject, "Format error. Try downloading the newest version.");
											 
											break;
										
										case "OK":
											// De-serialization
											GetSimServerResponse response = JsonImpl.fromJson(content, GetSimServerResponse.class);
											AmortizationType amortization = response.getAmortizationType(); 
											List<Repayment> listrepay=  response.getRepayments();   
											System.out.print(listrepay.toString());
											String amort = amortization.toString();
											

											String[][] datas = (String[][]) new String[listrepay.size()][6];

											for(int i=0 ; i < listrepay.size() ; i++) {
												datas[i][0] =  Integer.toString(i+1);
												datas[i][1] = listrepay.get(i).getDate().toString();
												datas[i][2] = Float.toString(listrepay.get(i).getCapital());
												datas[i][3] =  Float.toString(listrepay.get(i).getInterest());
												datas[i][4] = Float.toString(listrepay.get(i).getInsurance());
												datas[i][5] = Float.toString(listrepay.get(i).getInsurance()+listrepay.get(i).getCapital() + listrepay.get(i).getInterest());

											}
											//String titretable[] = new String [] {
											//		"ECHEANCE", "DATE", 
											//};
											//tblRepay.set; 

													String col[] = {"ECHEANCE","DATE","MENSUALITE","DONT INTERETS","ASSURANCE","MENSUALITE TOTAL"}; 
													DefaultTableModel model = new DefaultTableModel(datas,col);
													tblRepay.setModel(model);
													tblRepay.setFillsViewportHeight(true);
													// Create the scroll pane and add the table to it. 
													// Add the scroll pane to this panel. 
											if (amort=="steady"){
												amort="CONSTANT";
											}else if(amort=="degressive") {
												amort="DEGRESSIF";
											}
											System.out.println(response);
											lblTitle.setText(response.getName());
											lblCustomer.setText(response.getAccountId());
											lblAge.setText(response.getAge());
											lblCapital.setText(Float.toString(response.getCapital()));
											lblTypeLoan.setText(response.getTypeSim());
											lblNumAccount.setText(response.getAcountNum());
											lblRepaymentConstant.setText(amort);
											lblRepaymentAmount.setText(Float.toString(response.getRepaymentConstant()));
											lblLoanRate.setText("0.5");
											lblInsuranceRate.setText("10000");
											lblEffectiveRate.setText("TEG");
											lblTotalCreditCost.setText("0");
											lblInterest.setText("Interet");
											lblInsurance.setText("0");
											lblApplicationFee.setText("0");
											//textPane.setText(listrepay.toString()); 

											
											break;
										
										default:
											throw new Exception("Unknown prefix");
										}
									} catch (Exception e1) {
										JOptionPane.showMessageDialog(thisObject, "Unknown response format. Please try again later or download the newest version.");
									}
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(thisObject, "Unable to connect to the server. Please try again later.");
								}
							}
						}).start();
					}
				});
			}
			
		});
		
	}
	/**
	 * calls the launch method on a set of demo Tabs.
	 * @param args : not used
	 */
	public static void main(String[] args) {
		/* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	MainResultGUI mr = null;
				try {
					try {
						mr = new MainResultGUI();
					} catch (NumberFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                mr.setVisible(true);
                
            }
        });
	}
}
