package account_select;


import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import model.query.ClientQuery;
import model.query.SearchAccountsQuery;
import model.response.GetAccountsServerResponse.Account;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JSeparator;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * An account selection GUI in which the accounts are fetched based on the name of their owners, and the advisor of their owners.
 * @see AccountSelectionGUI
 * @author Kappa-V
 * @version R3 sprint 3 - 08/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized.
public abstract class CustomerChoiceGUI extends AccountSelectionGUI {
	private final JTextField lastNameField;
	private final JTextField firstNameField;
	private final JCheckBox myCustomersCheckbox;
	
	/**
	 * Creates the panel and all its components.
	 */
	public CustomerChoiceGUI() {
		super();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{232, 86, 91, 0};
		gridBagLayout.rowHeights = new int[]{20, 20, 21, 23, 31, 2, 143, 23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
				
		JLabel lastNameLabel = new JLabel("Nom :");
		GridBagConstraints gbc_lastNameLabel = new GridBagConstraints();
		gbc_lastNameLabel.anchor = GridBagConstraints.EAST;
		gbc_lastNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lastNameLabel.gridx = 0;
		gbc_lastNameLabel.gridy = 0;
		add(lastNameLabel, gbc_lastNameLabel);
		
		lastNameField = new JTextField();
		GridBagConstraints gbc_lastNameField = new GridBagConstraints();
		gbc_lastNameField.anchor = GridBagConstraints.NORTHWEST;
		gbc_lastNameField.insets = new Insets(0, 0, 5, 5);
		gbc_lastNameField.gridx = 1;
		gbc_lastNameField.gridy = 0;
		add(lastNameField, gbc_lastNameField);
		lastNameField.setColumns(10);
		
		JLabel firstNameLabel = new JLabel("Pr�nom :");
		GridBagConstraints gbc_firstNameLabel = new GridBagConstraints();
		gbc_firstNameLabel.anchor = GridBagConstraints.EAST;
		gbc_firstNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstNameLabel.gridx = 0;
		gbc_firstNameLabel.gridy = 1;
		add(firstNameLabel, gbc_firstNameLabel);
		
		firstNameField = new JTextField();
		GridBagConstraints gbc_firstNameField = new GridBagConstraints();
		gbc_firstNameField.anchor = GridBagConstraints.NORTHWEST;
		gbc_firstNameField.insets = new Insets(0, 0, 5, 5);
		gbc_firstNameField.gridx = 1;
		gbc_firstNameField.gridy = 1;
		add(firstNameField, gbc_firstNameField);
		firstNameField.setColumns(10);
		
		JLabel myCustomersLabel = new JLabel("Mes clients uniquement :");
		GridBagConstraints gbc_myCustomersLabel = new GridBagConstraints();
		gbc_myCustomersLabel.anchor = GridBagConstraints.EAST;
		gbc_myCustomersLabel.insets = new Insets(0, 0, 5, 5);
		gbc_myCustomersLabel.gridx = 0;
		gbc_myCustomersLabel.gridy = 2;
		add(myCustomersLabel, gbc_myCustomersLabel);
		
		myCustomersCheckbox = new JCheckBox("");
		GridBagConstraints gbc_myCustomersCheckbox = new GridBagConstraints();
		gbc_myCustomersCheckbox.anchor = GridBagConstraints.NORTH;
		gbc_myCustomersCheckbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_myCustomersCheckbox.insets = new Insets(0, 0, 5, 0);
		gbc_myCustomersCheckbox.gridwidth = 2;
		gbc_myCustomersCheckbox.gridx = 1;
		gbc_myCustomersCheckbox.gridy = 2;
		add(myCustomersCheckbox, gbc_myCustomersCheckbox);

		
		// AccountSelectionGUI components management 
		JButton sendButton = new JButton("Chercher un compte");
		setSendQueryButton(sendButton);
		GridBagConstraints gbc_sendButton = new GridBagConstraints();
		gbc_sendButton.anchor = GridBagConstraints.NORTH;
		gbc_sendButton.insets = new Insets(0, 0, 5, 0);
		gbc_sendButton.gridwidth = 3;
		gbc_sendButton.gridx = 0;
		gbc_sendButton.gridy = 3;
		add(getSendQueryButton(), gbc_sendButton);
//		SwingUtilities.getRootPane(getSendQueryButton()).setDefaultButton(getSendQueryButton()); // Makes it possible to press enter // TODO : raises a nullpointerexception. Fix.
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.anchor = GridBagConstraints.NORTH;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridwidth = 3;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 5;
		add(separator, gbc_separator);
		
		JList<Account> resultsList = new JList<>();
		setResultsPanel(resultsList);
		GridBagConstraints gbc_resultsList = new GridBagConstraints();
		gbc_resultsList.fill = GridBagConstraints.BOTH;
		gbc_resultsList.insets = new Insets(0, 0, 5, 0);
		gbc_resultsList.gridwidth = 3;
		gbc_resultsList.gridx = 0;
		gbc_resultsList.gridy = 6;
		add(resultsList, gbc_resultsList);
		
		
		JButton button_1 = new JButton("S�lectionner");
		setSelectButton(button_1);
		getSelectButton().setEnabled(false);
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.anchor = GridBagConstraints.EAST;
		gbc_1.gridx = 2;
		gbc_1.gridy = 7;
		add(getSelectButton(), gbc_1);
		
		
	}

	
	@Override
	protected ClientQuery generateQuery() {
		return new SearchAccountsQuery(firstNameField.getText(), lastNameField.getText(), myCustomersCheckbox.isSelected());
	}
}

