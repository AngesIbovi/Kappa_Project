package comparison.elements;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import model.response.GetSimServerResponse;
import model.response.GetSimServerResponse.Repayment;

import javax.swing.JTable;

/**
 * A table which gives an overview of all the main volumes of each simulation :
 * capital, total insurance, total interests, and global total. 
 * @author Kappa-V
 * @version R3 sprint 3 - 16/05/2016
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class MainVolumesElement extends SimulationComparisonElement {
	/**
	 * Inner class. The calculations are done here, so that they don't have 
	 * to be done again every time a swing event is fired.
	 */
	private class Row {
		private String name;
		private String capital;
		private String insurance;
		private String interests;
		private String total;
		
		public Row(String name, double capital, double insurance, double interests, double total) {
			super();
			this.name = name;
			this.capital = String.format("%.2f", capital);
			this.insurance = String.format("%.2f", insurance);;
			this.interests = String.format("%.2f", interests);;
			this.total = String.format("%.2f", total);;
		}
		
		public String getName() {
			return name;
		}
		public String getCapital() {
			return capital;
		}
		public String getInsurance() {
			return insurance;
		}
		public String getInterests() {
			return interests;
		}
		public String getTotal() {
			return total;
		}
	}
	
	private List<Row> rows;
	
	@Override
	public void setSimulations(List<GetSimServerResponse> simulations) {
		List<Row> newRows = new ArrayList<>();
		for(GetSimServerResponse simulation : simulations) {
			// Measurement
			double totalInsurance = 0;
			double totalInterests = 0;
			for(Repayment p : simulation.getRepayments()) {
				totalInsurance += p.getInsurance();
				totalInterests += p.getInterest();
			}
			double total = simulation.getCapital() + totalInsurance + totalInterests;
			
			// Generating the row object
			newRows.add(new Row(simulation.getName(), simulation.getCapital(), 
					totalInsurance, totalInterests, total));
		}
		
		rows = newRows;
	}

	/**
	 * Create the panel, and positions the swing components
	 */
	public MainVolumesElement() {
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{15, (int)(maxDimensions.getWidth() -30), 0};
		gridBagLayout.rowHeights = new int[]{14, 100, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel titleLabel = new JLabel("Volumes principaux");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 1;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		AbstractTableModel tableModel = new AbstractTableModel() {
			private final String[] headers = {"Nom de la simulation", "Capital", "Total assurance", "Total int�r�ts", "Total g�n�ral"};
			
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
				return rows.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Row row = rows.get(rowIndex);
				
				switch(columnIndex) {
				case 0: // Name
					return row.getName();
				case 1: // Capital
					return row.getCapital();
				case 2: // Insurance
					return row.getInsurance();
				case 3: // Interests
					return row.getInterests();
				case 4: // Total
					return row.getTotal();
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
