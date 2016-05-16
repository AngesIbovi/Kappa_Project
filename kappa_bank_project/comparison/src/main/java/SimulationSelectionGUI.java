

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.SessionInformation;
import model.query.ClientQuery;
import model.query.GetSimsQuery;
import model.response.GetAccountsServerResponse.Account;
import model.response.GetSimsServerResponse;
import model.response.GetSimsServerResponse.SimulationIdentifier;
import util.JsonImpl;
import view.SessionSpecific;

/**
 * A GUI in which the user selects simulations to compare from a list.
 * @author Kappa-V
 * @version R3 Sprint 3 - 13/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized
public abstract class SimulationSelectionGUI extends JPanel implements SessionSpecific {
	private final SimulationSelectionGUI thisObject = this;
	private final JButton selectButton;
	private final JButton returnButton;
	private final JList<SimulationIdentifier> resultsList;
	private Socket socket;
	
	/**
	 * This method is called when the user has selected an account and pressed the select button.</br>
	 * The idea is that the class which instantiates a SimulationSelectionGUI defines the behavior is wants here. 
	 * @param L - The List of SimulationIdentifiers which have been selected. Cannot be null, but can be empty.
	 */
	public abstract void onSelect(List<SimulationIdentifier> L);
	
	/**
	 * This method is called when the user presses the return button.</br>
	 * The idea is that the class which instantiates a SimulationSelectionGUI defines the behavior it wants here.</br>
	 * If the behavior you want to define uses long calculations, waits for user input or network responses, do not forget to start a new thread.
	 */
	public abstract void onReturn();
	
	/**
	 * Fetches the simulations from the server. Call whenever you enter this view.</br>
	 * setSessionInformation must have been called prior to calling this method.
	 */
	public void getSimulations(final List<Account> L) {
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
				final Vector<SimulationIdentifier> simIds = new Vector<>();
				for(Account A : L) {
					ClientQuery query = new GetSimsQuery(A.getAccount_id());
					out.println(query.toString());
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
							GetSimsServerResponse response = JsonImpl.fromJson(content, GetSimsServerResponse.class);
							simIds.addAll(response.getSimulations());
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
				}
				
				// GUI update
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						resultsList.setListData(simIds);
						selectButton.setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Constructor. Creates the different swing elements.
	 */
	public SimulationSelectionGUI() {
		// Layout
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{190, 90, 67, 0};
		gridBagLayout.rowHeights = new int[]{20, 202, 23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		// List
		resultsList = new JList<>();
		GridBagConstraints gbc_resultsList = new GridBagConstraints();
		gbc_resultsList.fill = GridBagConstraints.BOTH;
		gbc_resultsList.insets = new Insets(0, 0, 5, 0);
		gbc_resultsList.gridwidth = 3;
		gbc_resultsList.gridx = 0;
		gbc_resultsList.gridy = 1;
		add(resultsList, gbc_resultsList);
		
		// Select button
		selectButton = new JButton("S�lectionner");
		selectButton.setEnabled(false);
		GridBagConstraints gbc_selectButton = new GridBagConstraints();
		gbc_selectButton.anchor = GridBagConstraints.EAST;
		gbc_selectButton.gridx = 2;
		gbc_selectButton.gridy = 2;
		add(selectButton, gbc_selectButton);
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelect(resultsList.getSelectedValuesList());
			}
		});
		
		// Return button
		returnButton = new JButton("Retour");
		GridBagConstraints gbc_returnButton = new GridBagConstraints();
		gbc_returnButton.anchor = GridBagConstraints.WEST;
		gbc_returnButton.gridx = 1;
		gbc_returnButton.gridy = 2;
		add(returnButton, gbc_returnButton);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReturn();
			}
		});
	}
	
	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		this.socket = sessionInformation.getSocket();
	}
}
