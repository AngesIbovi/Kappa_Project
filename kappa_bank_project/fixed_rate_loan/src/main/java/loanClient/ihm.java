package loanClient;

import javax.swing.JPanel;

import model.SessionInformation;
import view.Tab;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

public class ihm extends Tab {

	/**
	 * Create the panel.
	 */
	public ihm() {
		super("Simuler un prêt à taux fixe", 1);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblHelloWorl = new JLabel("hello momo");
		GridBagConstraints gbc_lblHelloWorld = new GridBagConstraints();
		gbc_lblHelloWorld.gridx = 7;
		gbc_lblHelloWorld.gridy = 4;
		add(lblHelloWorl, gbc_lblHelloWorld);
		
		//Tout ce qui n'a pas besoin de session information

	}

	public void setSessionInformation(SessionInformation sessionInformation) {
		// TODO Auto-generated method stub
		// tout ce qui a besoin de SessionInformation
	}

}
