package comparison;



import java.awt.CardLayout;
import java.util.List;

import javax.swing.JOptionPane;

import comparison.account_select.AccountSelectionGUI;
import comparison.account_select.LoginChoiceGUI;
import comparison.account_select.AdvisorLoginChoiceGUI;
import model.SessionInformation;
import model.response.GetAccountsServerResponse.Account;
import model.response.GetSimsServerResponse.SimulationIdentifier;
import view.Tab;

/**
 * The Tab for the comparison Use Case.
 * @see Tab
 * @author Kappa-V
 * @version R3 sprint 3 - 13/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized
public class ComparisonGUI extends Tab {
	private ComparisonGUI thisObject = this;
	private AccountSelectionGUI accountSelectionGUI;
	private SimulationSelectionGUI simulationSelectionGUI;
	private SimulationComparisonGUI simulationComparisonGUI;
	
	private final CardLayout cards = new CardLayout();
	private final String accountSelectionCardName = "ACC.SEL";
	private final String simulationSelectionCardName = "SIM.SEL";
	private final String simulationComparisonCardName = "SIM.CMP";
	
	public ComparisonGUI() {
		super("Comparer des simulations", 1);
	}

	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		// Account Selection : the nature of this card depends on the user's authorization level.
		switch (sessionInformation.authorization_level) {
		case 1: // For simple customers
			accountSelectionGUI = new LoginChoiceGUI() {
				public void onSelect(List<Account> L) {
					if(L.size() == 0) {
						JOptionPane.showMessageDialog(thisObject, "Veuillez sélectionner au moins un compte.");
					} else {
						simulationSelectionGUI.getSimulations(L);
						cards.show(thisObject, simulationSelectionCardName);
					}
				}
			};
			break;
		default: // For bank employees
			accountSelectionGUI = new AdvisorLoginChoiceGUI() {
				public void onSelect(List<Account> L) {
					if(L.size() == 0) {
						JOptionPane.showMessageDialog(thisObject, "Veuillez sélectionner au moins un compte.");
					} else {
						simulationSelectionGUI.getSimulations(L);
						cards.show(thisObject, simulationSelectionCardName);
					}
				}
			};
			break;
		}
		
		// Simulation selection
		simulationSelectionGUI = new SimulationSelectionGUI() {
			public void onSelect(List<SimulationIdentifier> L) {
				if(L.size() < 2) {
					JOptionPane.showMessageDialog(thisObject, "Veuillez sélectionner au moins deux simulations. Utilisez ctrl+clic pour en sélectionner plusieurs à la fois.");
				} else {
					simulationComparisonGUI.getSimulations(L);
					cards.show(thisObject, simulationComparisonCardName);
				}
			}

			public void onReturn() {
				cards.show(thisObject, accountSelectionCardName);
			}
		};
		
		// Simulation comparison
		simulationComparisonGUI = new SimulationComparisonGUI() {
			public void onReturn() {
				cards.show(thisObject, simulationSelectionCardName);
			}
		};
		

		// Session Information dispatching
		accountSelectionGUI.setSessionInformation(sessionInformation);
		simulationSelectionGUI.setSessionInformation(sessionInformation);
		simulationComparisonGUI.setSessionInformation(sessionInformation);
		
		// Card layout
		this.setLayout(cards);
		this.add(accountSelectionGUI, accountSelectionCardName);
		this.add(simulationSelectionGUI, simulationSelectionCardName);
		this.add(simulationComparisonGUI, simulationComparisonCardName);
		cards.show(thisObject, accountSelectionCardName);
	}
}
