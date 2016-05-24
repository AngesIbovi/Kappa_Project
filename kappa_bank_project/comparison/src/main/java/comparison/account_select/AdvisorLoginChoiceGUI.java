package comparison.account_select;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import model.SessionInformation;
import model.query.ClientQuery;
import model.response.GetAccountsServerResponse.Account;

/**
 * An account selection GUI in which contains a LoginChoiceGUI, but gives the option to use a CustomerChoiceGUI instead.
 * @see AccountSelectionGUI
 * @see LoginChoiceGUI
 * @see CustomerChoiceGUI
 * @author Kappa-V
 * @version R3 sprint 3 - 09/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized
public abstract class AdvisorLoginChoiceGUI extends AccountSelectionGUI {
	private final AccountSelectionGUI thisObject = this;
	private final AccountSelectionGUI login;
	private final AccountSelectionGUI advanced;
	
	private CardLayout cards = new CardLayout();
	private final String loginCardName = "LOGIN";
	private final String advancedCardName = "ADVANCED";
	private AccountSelectionGUI currentCard;
	
	public AdvisorLoginChoiceGUI() {
		// Cards initialization
		login = new LoginChoiceGUI() {
			public void onSelect(List<Account> A) {
				thisObject.onSelect(A);
			}
		};
		advanced = new CustomerChoiceGUI() {
			public void onSelect(List<Account> A) {
				thisObject.onSelect(A);
			}
		};
		
		// Card layout initialization
		this.setLayout(cards);
		this.add(login, loginCardName);
		this.add(advanced, advancedCardName);
		cards.show(this, loginCardName);
		currentCard = login;
		
		// Buttons, and event listeners to switch between cards
		JButton advancedButton = new JButton("Recherche avancée");
		GridBagConstraints gbc_advancedButton = new GridBagConstraints();
		gbc_advancedButton.anchor = GridBagConstraints.WEST;
		gbc_advancedButton.insets = new Insets(0, 0, 0, 5);
		gbc_advancedButton.gridx = 0;
		gbc_advancedButton.gridy = 4;
		login.add(advancedButton, gbc_advancedButton);
		advancedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cards.show(thisObject, advancedCardName);
			}
		});
		

		
		JButton returnButton = new JButton("Retour");
		GridBagConstraints gbc_returnButton = new GridBagConstraints();
		gbc_returnButton.anchor = GridBagConstraints.WEST;
		gbc_returnButton.insets = new Insets(0, 0, 0, 5);
		gbc_returnButton.gridx = 0;
		gbc_returnButton.gridy = 7;
		advanced.add(returnButton, gbc_returnButton);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cards.show(thisObject, loginCardName);
			}
		});
	}
	
	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		login.setSessionInformation(sessionInformation);
		advanced.setSessionInformation(sessionInformation);
	}

	@Override
	protected ClientQuery generateQuery() {
		return currentCard.generateQuery();
	}
}
