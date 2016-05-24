package comparison.elements;

import java.util.List;

import model.response.GetSimServerResponse;
import model.response.GetSimServerResponse.Repayment;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;

/**
 * A graph which allows the user to compare the weights of repayments compared to the customer's income.
 * @author Kappa-V
 * @version R3 sprint 4 - 24/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized
public class RepaymentComparisonElement extends SimulationComparisonElement {
	private final static String defaultGraphCardName = "DEF";
	private final static String normalizedGraphCardName = "NRM";
	private final static CardLayout cards = new CardLayout();
	
	private final ChartPanel defaultChartPanel;
	private final ChartPanel normalizedChartPanel;
	
	public RepaymentComparisonElement() {
		Dimension maxDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, (int) maxDimensions.getWidth() - 300, 100, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel titleLabel = new JLabel("Poids des mensualités");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		final JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		panel.setLayout(cards);
		
		final JCheckBox normalizeCheckbox = new JCheckBox("Normaliser");
		normalizeCheckbox.setToolTipText("Dans le graphe normalisé, le montant des mensualités est divisé par le nombre de mois entre chaque mensualité.");
		GridBagConstraints gbc_normalizeCheckbox = new GridBagConstraints();
		gbc_normalizeCheckbox.insets = new Insets(0, 0, 5, 0);
		gbc_normalizeCheckbox.anchor = GridBagConstraints.EAST;
		gbc_normalizeCheckbox.gridx = 2;
		gbc_normalizeCheckbox.gridy = 0;
		add(normalizeCheckbox, gbc_normalizeCheckbox);
		normalizeCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				if(normalizeCheckbox.isSelected()) {
					cards.show(panel, normalizedGraphCardName);
				} else {
					cards.show(panel, defaultGraphCardName);
				}
			}
		});
		
		// Cards. BorderLayouts allow the chart to take up all available space.
		JPanel defaultGraphCard = new JPanel();
		defaultGraphCard.setLayout(new BorderLayout());
		panel.add(defaultGraphCard, defaultGraphCardName);
		
		JPanel normalizedGraphCard = new JPanel();
		normalizedGraphCard.setLayout(new BorderLayout());
		panel.add(normalizedGraphCard, normalizedGraphCardName);
		
		cards.show(panel, defaultGraphCardName);
		
		// Charts
        XYSeriesCollection dataset = new XYSeriesCollection();
		JFreeChart emptyChart = ChartFactory.createXYLineChart("", "Mensualités", "Montants des mensualités", dataset, PlotOrientation.VERTICAL, true, false, false); // Just so that there are no exceptions
		defaultChartPanel = new ChartPanel(emptyChart);
		defaultGraphCard.add(defaultChartPanel, BorderLayout.CENTER);
		normalizedChartPanel = new ChartPanel(emptyChart);
		normalizedGraphCard.add(normalizedChartPanel, BorderLayout.CENTER);
	}

	@Override
	public void setSimulations(List<GetSimServerResponse> simulations) {
		// Chart generation
		XYSeriesCollection defaultDataset = new XYSeriesCollection();
		XYSeriesCollection normalizedDataset = new XYSeriesCollection();
		
		for(GetSimServerResponse simulation : simulations) {
			XYSeries defaultSeries = new XYSeries(simulation.getName());
			XYSeries normalizedSeries = new XYSeries(simulation.getName());
			
			for(Repayment repayment : simulation.getRepayments()) {
				double total = repayment.getCapital() + repayment.getInsurance() + repayment.getInterest();
				double normalizedTotal = total * simulation.getRepaymentFrequency() / 12;
				
				defaultSeries.add(repayment.getDate().getTime(), total);
				normalizedSeries.add(repayment.getDate().getTime(), normalizedTotal);
			}
			
			defaultDataset.addSeries(defaultSeries);
			normalizedDataset.addSeries(normalizedSeries);
		}
		
		JFreeChart defaultChart = ChartFactory.createXYLineChart("", "Mensualités", "Montants des mensualités", defaultDataset, PlotOrientation.VERTICAL, true, false, false);
		JFreeChart normalizedChart = ChartFactory.createXYLineChart("", "Mensualités", "Montants des mensualités (normalisés)", normalizedDataset, PlotOrientation.VERTICAL, true, false, false);
		
		// Style
		XYPlot defaultPlot = (XYPlot) defaultChart.getPlot();
		XYLineAndShapeRenderer defaultRenderer = (XYLineAndShapeRenderer) defaultPlot.getRenderer();
		defaultRenderer.setBaseShapesVisible(true);

		XYPlot normalizedPlot = (XYPlot) normalizedChart.getPlot();
		XYLineAndShapeRenderer normalizedRenderer = (XYLineAndShapeRenderer) normalizedPlot.getRenderer();
		normalizedRenderer.setBaseShapesVisible(true);
		
		defaultChart.setAntiAlias(true);
		defaultChart.setTextAntiAlias(true);
		
		normalizedChart.setAntiAlias(true);
		normalizedChart.setTextAntiAlias(true);
		
		// GUI update
		defaultChartPanel.setChart(defaultChart);
		normalizedChartPanel.setChart(normalizedChart);
	}
}