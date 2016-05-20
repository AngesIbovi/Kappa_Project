package comparison.elements;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.sql.Date;
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
public class AmortizationElement extends SimulationComparisonElement {
	
	/**
	 * Inner class. The calculations are done here, so that they don't have 
	 * to be done again every time a swing event is fired.
	 */
	private class Row {
		private String name;
		private String amortizationType;
		private String totalDuration;
		private Date firstRepayment;
		private Date lastRepayment;
		private int repaymentFrequency;
		private int totalRepayments;
		
		public Row(GetSimServerResponse sim) {
			this.name = sim.getName();
			switch(sim.getAmortizationType()) {
			case degressive:
				this.amortizationType = "D�gressives";
				break;
			case steady:
				this.amortizationType = "Constantes";
				break;
			}
			
			for(Repayment p : sim.getRepayments()) {
				if((firstRepayment == null) || (firstRepayment.after(p.getDate())))
					firstRepayment = p.getDate();
				if((lastRepayment == null) || (lastRepayment.before(p.getDate())))
					lastRepayment = p.getDate();
			}

			int totalMonths = sim.getRemainingRepayments() * 12 / sim.getRepaymentFrequency();
			int totalYears = totalMonths / 12;
			totalMonths = totalMonths % 12;
			totalDuration = totalYears + " ann�es, et " + totalMonths + " mois";
			
			repaymentFrequency = sim.getRepaymentFrequency();
			totalRepayments = sim.getRemainingRepayments();
		}

		public String getName() {
			return name;
		}
		public String getAmortizationType() {
			return amortizationType;
		}
		public String getTotalDuration() {
			return totalDuration;
		}
		public Date getFirstRepayment() {
			return firstRepayment;
		}
		public Date getLastRepayment() {
			return lastRepayment;
		}
		public int getRepaymentFrequency() {
			return repaymentFrequency;
		}
		public int getTotalRepayments() {
			return totalRepayments;
		}
	}

	private List<Row> rows;

	@Override
	public void setSimulations(List<GetSimServerResponse> simulations) {
		List<Row> newRows = new ArrayList<>();
		for(GetSimServerResponse simulation : simulations) {
			newRows.add(new Row(simulation));
		}
		
		rows = newRows;
	}

	/**
	 * Create the panel, and positions the swing components
	 */
	public AmortizationElement() {
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{15, (int)(maxDimensions.getWidth() -30), 0};
		gridBagLayout.rowHeights = new int[]{14, 100, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel titleLabel = new JLabel("Type d'amortissement");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 1;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		AbstractTableModel tableModel = new AbstractTableModel() {
			private final String[] headers = {"Nom de la simulation", "Type de mensualit�s", "Dur�e du pr�t", 
					"Premi�re mensualit�", "Derni�re mensualit�", "Nombre de mensualit�s par an", "Nombre de mensualit�s total"};
			
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
				case 1: // amortizationType
					return row.getAmortizationType();
				case 2: // totalDuration
					return row.getTotalDuration();
				case 3: // firstRepayment
					return row.getFirstRepayment();
				case 4: // lastRepayment
					return row.getLastRepayment();
				case 5: // repaymentFrequency
					return row.getRepaymentFrequency();
				case 6: // totalRepayments
					return row.getTotalRepayments();
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
