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
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.JList;

public class ihm extends Tab {
	private final ihm thisObject = this;
	private final IHM2 ihm2;
	private final IHM3 ihm3;
	
	private CardLayout cards = new CardLayout();
	private final String ihm2CardName = "LOGIN";
	private final String ihm3CardName = "ADVANCED";
	private IHM2 currentCard;
	private Socket socket;
	Account accountSelected;
	
	
	public ihm() {
		super("Simuler un prêt à taux fixe", 1);

		// Cards initialization
		ihm2 = new IHM2() {
			public void onSelect(List<Account> A) {
				//
			}
		};
		GridBagLayout gridBagLayout = (GridBagLayout) ihm2.getLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, -39, 330, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		ihm3 = new IHM3() {
			public void onSelect(List<Account> A) {
				//
				}
			};
		ihm2.socket = this.socket;
		
		// Card layout initialization
		this.setLayout(cards);
		this.add(ihm2, ihm2CardName);
		this.add(ihm3, ihm3CardName);
		cards.show(this, ihm2CardName);
		currentCard = ihm2;
		
		// Buttons, and event listeners to switch between cards
		JButton advancedButton = new JButton("Simuler");
		GridBagConstraints gbc_advancedButton = new GridBagConstraints();
		gbc_advancedButton.anchor = GridBagConstraints.WEST;
		gbc_advancedButton.insets = new Insets(0, 0, 5, 7);
		gbc_advancedButton.gridx = 7;
		gbc_advancedButton.gridy = 4;
		ihm2.add(advancedButton, gbc_advancedButton);
		advancedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				 accountSelected = ihm2.list.getSelectedValuesList().get(0);
				 ihm3.accountSelected = accountSelected;
				 ihm3.lblNewLabel_1.setText( "id_compte: " + accountSelected.getAccount_id() + "        " + "Name: " + accountSelected.getName() + "        " + "Numéro de commpte: " +accountSelected.getAccount_num());
				cards.show(thisObject, ihm3CardName);
			}
		});
		
		
		JButton returnButton = new JButton("Retour");
		GridBagConstraints gbc_returnButton = new GridBagConstraints();
		gbc_returnButton.anchor = GridBagConstraints.WEST;
		gbc_returnButton.insets = new Insets(0, 0, 0, 20);
		gbc_returnButton.gridx = 20;
		gbc_returnButton.gridy = 7;
		ihm3.add(returnButton, gbc_returnButton);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cards.show(thisObject, ihm2CardName);
			}
		});
	}



	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		// TODO Auto-generated method stub
		this.socket = sessionInformation.getSocket();
		ihm2.socket = this.socket;
		System.out.println("this.socket:"+this.socket);
		
	}
}
