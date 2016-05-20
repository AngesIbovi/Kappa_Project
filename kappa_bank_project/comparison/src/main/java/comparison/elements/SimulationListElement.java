package comparison.elements;


import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.util.List;

import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import model.response.GetSimServerResponse;

import javax.swing.JTable;

/**
 * A table which gives an overview of all the main informations of each simulation (name and loan type),
 * and gives the user the possibility to validate one of the simulations and transform it into a real loan. 
 * @author Kappa-V
 * @version R3 sprint 3 - 16/05/2016
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class SimulationListElement extends SimulationComparisonElement {
	private List<GetSimServerResponse> simulations;
	
	@Override
	public void setSimulations(List<GetSimServerResponse> simulations) {
		this.simulations = simulations;
	}

	/**
	 * Create the panel, and positions the swing components
	 */
	public SimulationListElement() {
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{15, (int)(maxDimensions.getWidth() -30), 0};
		gridBagLayout.rowHeights = new int[]{14, 100, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel titleLabel = new JLabel("Liste des simulations compar�es");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 1;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		AbstractTableModel tableModel = new AbstractTableModel() {
			private final String[] headers = {"Nom de la simulation", "Type de pr�t", "Actions"};
			
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
					return "TODO"; // TODO
				case 2: //
					return "TODO"; // TODO
				default:
					return null; // Normally doesn't happen
				}
			}
		};
		
		JTable table = new JTable(tableModel);
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
	}
}
