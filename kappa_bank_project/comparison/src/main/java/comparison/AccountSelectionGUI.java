package comparison;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.SessionInformation;
import model.query.ClientQuery;
import model.response.GetAccountsServerResponse;
import model.response.GetAccountsServerResponse.Account;
import util.JsonImpl;
import view.SessionSpecific;

/**
 * An abstract class for JPanels which are used to go fetch account numbers from the server. There are at least two ways to do this,
 * but the communication code is the same, so it is factorized here.</br>
 * The basic idea is that subclasses should implement a constructor with the layout definition, including at least the components
 * which have a setter here, plus their search parameters. Those search parameters will be used in their implementation of the 
 * generateQuery() method.</br>
 * onSelect should stay abstract in subclasses, so that it can be defined by the class which uses an AccountSelectionGUI.
 * @author Kappa-V
 * @version R3 sprint 3 - 09/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized.
public abstract class AccountSelectionGUI extends JPanel implements SessionSpecific {
	private JButton sendQueryButton;
	private JList<Account> resultsList;
	private JButton selectButton;
	
	/**
	 * This method is used by the setSessionInformation method. It is used to grant the ability to subclasses to re-use 
	 * the setSessionInformation method. Subclasses should override the constructor, and this method.</br>
	 * @return the query object
	 */
	protected abstract ClientQuery generateQuery();
	
	/**
	 * This method is called when the user has selected an account and pressed the select button.</br>
	 * It should stay abstract in AccountSelectionGUI's subclasses so that the object which uses an 
	 * AccountSelectionGUI can define the onSelect behavior itself.
	 * @param A : the chosen accounts. Cannot be null, but can be empty.
	 */
	public abstract void onSelect(List<Account> A);
	
	// Getters and Setters
	public void setSendQueryButton(JButton sendQueryButton) {
		this.sendQueryButton = sendQueryButton;
	}
	public void setResultsPanel(JList<Account> resultsList) {
		this.resultsList = resultsList;
	}
	public void setSelectButton(JButton selectButton) {
		this.selectButton = selectButton;
	}
	public JButton getSendQueryButton() {
		return sendQueryButton;
	}
	public JList<Account> getResultsPanel() {
		return resultsList;
	}
	public JButton getSelectButton() {
		return selectButton;
	}
	
	/**
	 * Creates the listeners
	 */
	@Override
	public void setSessionInformation(final SessionInformation sessionInformation) {
		final AccountSelectionGUI thisObject = this;
		
		sendQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// All network operations are carried out in a thread so that the GUI doesn't freeze.
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
							return;
						}
						
						// Server transaction
						ClientQuery query = generateQuery();
						out.println(query.toString());
						GetAccountsServerResponse response;
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
								response = JsonImpl.fromJson(content, GetAccountsServerResponse.class);
								break;
							default:
								throw new Exception("ERR or UNAUTHORIZED");
							}
						} catch (IOException e) {
							JOptionPane.showMessageDialog(thisObject, "Erreur: Connexion au serveur interrompue. Vérifiez votre connection Internet, puis essayez de vous re-connecter.");
							return;
						} catch (Exception e) {
							JOptionPane.showMessageDialog(thisObject, "Erreur: Essayez de télécharger la nouvelle version de ce logiciel.");
							return;
						}
						
						// GUI update
						final Vector<Account> accounts = new Vector<>(response.getAccounts());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								resultsList.setListData(accounts);
							}
						});
						selectButton.setEnabled(true);
					}
				}).start();
			}
		});
		
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// All network operations are carried out in a thread so that the GUI doesn't freeze.
				new Thread(new Runnable() {
					public void run() {
						onSelect(resultsList.getSelectedValuesList());
					}
				}).start();
			}
		});
	}
}
