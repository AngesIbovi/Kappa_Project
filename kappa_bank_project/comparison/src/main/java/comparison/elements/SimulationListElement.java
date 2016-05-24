package comparison.elements;


import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;

import javax.swing.JScrollPane;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import model.SessionInformation;
import model.query.SignLoanQuery;
import model.response.GetSimServerResponse;
import model.response.SignLoanServerResponse;
import util.JsonImpl;
import view.SessionSpecific;

import javax.swing.JTable;

/**
 * A table which gives an overview of all the main informations of each simulation (name and loan type),
 * and gives the user the possibility to validate one of the simulations and transform it into a real loan. 
 * @author Kappa-V
 * @version R3 sprint 3 - 16/05/2016
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class SimulationListElement extends SimulationComparisonElement implements SessionSpecific {
	private final SimulationListElement thisObject = this;
	private final JTable table;
	private final JButton selectButton;
	private List<GetSimServerResponse> simulations;
	
	@Override
	public void setSimulations(final List<GetSimServerResponse> simulations) {
		this.simulations = simulations;
		
		table.setModel(new AbstractTableModel() {
			private final String[] headers = {"Nom de la simulation", "Type de prêt"};
			
			@Override
			public String getColumnName(int columnIndex){
				return headers[columnIndex];
			}
			
			@Override
			public int getColumnCount() {
				return headers.length;
			}

			@Override
			public int getRowCount() {
				return simulations.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				GetSimServerResponse simulation = simulations.get(rowIndex);
				
				switch(columnIndex) {
				case 0: // Name
					return simulation.getName();
				case 1: // Loan Type
					return simulation.getTypeSim();
				default:
					return null; // Normally doesn't happen
				}
			}
		});
	}

	/**
	 * Create the panel, and positions the swing components
	 */
	public SimulationListElement() {
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{15, (int)(maxDimensions.getWidth() -30), 0};
		gridBagLayout.rowHeights = new int[]{14, 100, 18, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel titleLabel = new JLabel("Liste des simulations comparées");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 1;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(gridBagLayout.columnWidths[1], gridBagLayout.rowHeights[1]));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.anchor = GridBagConstraints.NORTH;
		gbc_scrollPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
		
		selectButton = new JButton("Valider une simulation");
		GridBagConstraints gbc_selectButton = new GridBagConstraints();
		gbc_selectButton.anchor = GridBagConstraints.WEST;
		gbc_selectButton.insets = new Insets(0, 0, 5, 0);
		gbc_selectButton.gridx = 1;
		gbc_selectButton.gridy = 2;
		add(selectButton, gbc_selectButton);
	}

	@Override
	public void setSessionInformation(final SessionInformation sessionInformation) {
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Checking the JTable for a valid selection
				int[] selectedRows = table.getSelectedRows();
				if(selectedRows.length == 0) {
					JOptionPane.showMessageDialog(thisObject, "Veuillez sélectionner une simulation à valider.");
					return;
				} else if (selectedRows.length > 1) {
					JOptionPane.showMessageDialog(thisObject, "Veuillez ne sélectionner qu'une seule simulation à valider.");
					return;
				}
				
				// Extracting the simId while handling all possible exceptions
				GetSimServerResponse sim;
				try {
					sim = simulations.get(selectedRows[0]);
					
					if(sim.getIs_reel()) {
						JOptionPane.showMessageDialog(thisObject, "Il ne s'agit pas d'une simulation, mais d'un prêt.");
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace(); // DEBUG
					JOptionPane.showMessageDialog(thisObject, "Erreur inconnue. Veuillez réessayer.");
					return;
				}
				
				// Asking the user for his or her password
				final JPasswordField passwordField = new JPasswordField();
				final JLabel wrongPasswordLabel = new JLabel();
				final JButton sendButton = new JButton();
				final String simId = sim.getId();
				final JDialog passwordDialog = generateEnterPasswordDialog(passwordField, wrongPasswordLabel, sendButton);
				
				sendButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Network operations must not be carried out in the event dispatch thread
						new Thread(new Runnable() {
							public void run() {
								// Flow initialization
								PrintWriter out;
								BufferedReader in;
								try {
									out = new PrintWriter(sessionInformation.getSocket().getOutputStream(), true);
									in = new BufferedReader(new InputStreamReader(sessionInformation.getSocket().getInputStream()));
								} catch (Exception e1) { // Reached if an IO exception occurs, or if the socket is not connected anymore
									JOptionPane.showMessageDialog(thisObject, "Erreur: connection au serveur interrompue. Vérifiez votre connection Internet, puis essayez de vous re-connecter.");
									passwordDialog.dispose();
									return;
								}
								
								// Server transaction
								SignLoanQuery query = new SignLoanQuery(simId, String.valueOf(passwordField.getPassword()));
								out.println(query.toString());
								SignLoanServerResponse response;
								try {
									String message = in.readLine();
									
									// Prefix/Content identification
									int prefixEnd = message.indexOf(' ');
									if(prefixEnd == -1) {
										throw new Exception("No prefix");
									}
									
									String prefix = message.substring(0, prefixEnd);
									String content = message.substring(prefixEnd + 1);
									
									switch (prefix) {
									case "OK":
										response = JsonImpl.fromJson(content, SignLoanServerResponse.class);
										break;
									default:
										throw new Exception("ERR or UNAUTHORIZED");
									}
								} catch (IOException e) {
									JOptionPane.showMessageDialog(thisObject, "Erreur: Connexion au serveur interrompue. Vérifiez votre connection Internet, puis essayez de vous re-connecter.");
									passwordDialog.dispose();
									return;
								} catch (Exception e) {
									JOptionPane.showMessageDialog(thisObject, "Erreur: Essayez de télécharger la nouvelle version de ce logiciel.");
									passwordDialog.dispose();
									return;
								}
								
								// GUI update
								switch (response.getStatus()) {
								case OK:
									passwordDialog.dispose();
									JOptionPane.showMessageDialog(thisObject, "Demande de prêt effectuée avec succès.");
									break;
								case KO:
									wrongPasswordLabel.setVisible(true);
									break;
								}
							}
						}).start();
					}
				});
			}
		});
	}
	
	private JDialog generateEnterPasswordDialog(JPasswordField passwordField, JLabel wrongPasswordLabel, JButton sendButton) {
		final JDialog passwordDialog = new JDialog();
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		passwordDialog.setBounds((int)maxDimensions.getWidth()/2 - 150, (int)maxDimensions.getHeight()/2 - 75, 300, 150);
		passwordDialog.setTitle("Sécurité - Saisie du mot de passe");
		passwordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container pane = passwordDialog.getContentPane();
		

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{118, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pane.setLayout(gridBagLayout);
		
		JLabel headerLabel = new JLabel("Veuillez saisir votre mot de passe à nouveau.");
		GridBagConstraints gbc_headerLabel = new GridBagConstraints();
		gbc_headerLabel.gridwidth = 2;
		gbc_headerLabel.insets = new Insets(0, 0, 5, 0);
		gbc_headerLabel.gridx = 0;
		gbc_headerLabel.gridy = 0;
		pane.add(headerLabel, gbc_headerLabel);
		
		JLabel passwordLabel = new JLabel("Mot de passe");
		GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
		gbc_passwordLabel.insets = new Insets(0, 0, 5, 5);
		gbc_passwordLabel.anchor = GridBagConstraints.EAST;
		gbc_passwordLabel.gridx = 0;
		gbc_passwordLabel.gridy = 1;
		pane.add(passwordLabel, gbc_passwordLabel);
		
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.anchor = GridBagConstraints.WEST;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		pane.add(passwordField, gbc_passwordField);
		passwordField.setColumns(10);
		
		sendButton.setText("Valider");
		GridBagConstraints gbc_sendButton = new GridBagConstraints();
		gbc_sendButton.gridwidth = 2;
		gbc_sendButton.gridx = 0;
		gbc_sendButton.gridy = 3;
		pane.add(sendButton, gbc_sendButton);
		
		wrongPasswordLabel.setText("Mauvais mot de passe.");
		wrongPasswordLabel.setForeground(Color.RED);
		wrongPasswordLabel.setVisible(false);
		GridBagConstraints gbc_wrongPasswordLabel = new GridBagConstraints();
		gbc_wrongPasswordLabel.gridwidth = 2;
		gbc_wrongPasswordLabel.insets = new Insets(0, 0, 5, 5);
		gbc_wrongPasswordLabel.gridx = 0;
		gbc_wrongPasswordLabel.gridy = 2;
		add(wrongPasswordLabel, gbc_wrongPasswordLabel);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				passwordDialog.setVisible(true);
			}
		});
		
		return passwordDialog;
	}
}
