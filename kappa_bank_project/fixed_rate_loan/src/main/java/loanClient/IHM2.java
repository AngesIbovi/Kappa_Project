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
import javax.swing.JList;

public class IHM2 extends Tab {
	private JTextField textField;
	public Socket socket;
	private final IHM2 thisObject = this;
	public final JList<Account> list;
	public Account selectedAccount; 


	/**
	 * Create the panel.
	 */
	public IHM2() {
		super("Simuler un prêt à taux fixe", 1);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblHelloWorl = new JLabel("Nom du client");
		GridBagConstraints gbc_lblHelloWorld = new GridBagConstraints();
		gbc_lblHelloWorld.insets = new Insets(0, 0, 5, 0);
		gbc_lblHelloWorld.gridx = 7;
		gbc_lblHelloWorld.gridy = 0;
		add(lblHelloWorl, gbc_lblHelloWorld);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 7;
		gbc_textField.gridy = 1;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		
		
		JButton btnChercherUnClient = new JButton("Chercher un client");
		GridBagConstraints gbc_btnChercherUnClient = new GridBagConstraints();
		gbc_btnChercherUnClient.insets = new Insets(0, 0, 5, 0);
		gbc_btnChercherUnClient.gridx = 7;
		gbc_btnChercherUnClient.gridy = 2;
		add(btnChercherUnClient, gbc_btnChercherUnClient);
		

		 list = new JList<>();
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(0, 0, 5, 0);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 7;
		gbc_list.gridy = 3;
		add(list, gbc_list);
		
		/*
		//Button
		final JButton btnSimuler = new JButton("Simuler");
		btnSimuler.setEnabled(false);
		btnSimuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Account accountSelected = list.getSelectedValuesList().get(0);
			}
		});
		
		GridBagConstraints gbc_btnAlle = new GridBagConstraints();
		gbc_btnAlle.insets = new Insets(0, 0, 5, 0);
		gbc_btnAlle.gridx = 7;
		gbc_btnAlle.gridy = 4;
		add(btnSimuler, gbc_btnAlle);
		*/
		
		
		btnChercherUnClient.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String userName = textField.getText();
				System.err.println("userName:"+userName);
				String content = "{\"cust_login\":\"Marc\"}";
				System.out.println("addActionListener_this.socket:"+socket);


				// All network operations are carried out in a thread so that the GUI doesn't freeze.
				new Thread(new Runnable() {
					public void run() {
						// Flow initialization
						PrintWriter out;
						BufferedReader in;
						try {
							out = new PrintWriter(socket.getOutputStream(), true);
							in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						} catch (Exception e1) { // Reached if an IO exception occurs, or if the socket is not connected anymore
							JOptionPane.showMessageDialog(thisObject, "Erreur: connection au serveur interrompue. V�rifiez votre connection Internet, puis essayez de vous re-connecter.");
							return;
						}

						// Server transaction
							ClientQuery query = new GetAccountsQuery(textField.getText());
							out.println(query.toString());
							final Vector<Account> accounts = new Vector<>();

							try {
								String message = in.readLine();
								
								// Prefix/Content identification
								int prefixEnd = message.indexOf(' ');
								if(prefixEnd == -1) {
									throw new Exception("No prefix");
								}
								
								String prefix = message.substring(0, prefixEnd);
								String content = message.substring(prefixEnd + 1);
								System.err.println("conentt:"+content);

								switch (prefix) {
								case "OK":
									GetAccountsServerResponseMO response = JsonImpl.fromJson(content, GetAccountsServerResponseMO.class);
									accounts.addAll(response.getAccounts());
									
									break;
								default:
									throw new Exception("ERR or UNAUTHORIZED");
								}
							} catch (IOException e) {
								JOptionPane.showMessageDialog(thisObject, "Erreur: Connexion au serveur interrompue. V�rifiez votre connection Internet, puis essayez de vous re-connecter.");
								return;
							} catch (Exception e) {
								JOptionPane.showMessageDialog(thisObject, "Erreur: Essayez de t�l�charger la nouvelle version de ce logiciel.");
								return;
							}
						
						
						// GUI update
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								list.setListData(accounts);
						//		btnSimuler.setEnabled(true);
							}
						});
						
					}
				}).start();
			
				
				
}
		});
		
	
		
		//Tout ce qui n'a pas besoin de session information

	}

	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		this.socket = sessionInformation.getSocket();
		System.out.println("this.socket:"+this.socket);
	}

}
