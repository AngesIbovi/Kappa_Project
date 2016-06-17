package org.view_print_results_loan;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import model.query.GetAllSimsQuery;
import model.query.GetSimQuery;
import model.response.GetAllSimsServerResponse;
import model.response.GetAllSimsServerResponse.SimulationIdentifier;
import model.simulation.Simulation;
import model.simulation.Simulation.AmortizationType;
import model.simulation.Repayment;
import util.KappaProperties;
import util.JsonImpl; 
 
/**
* A Main Jframe used for the displaying of chart results.
* @version R3 Sprint 3 - 19/05/2016
* @Author Kappa-M 
*/
@SuppressWarnings("serial")
public class ChartResult extends JFrame {
	JPanel chartPanel =new JPanel();

	public ChartResult(final Socket socket, final String args) throws ClassNotFoundException, SQLException, NumberFormatException, IOException  {
		super("Graphique des résultats");
		final ChartResult thisObject = this;  
		// Initializing tools  
				/* Network connection */
		
		// Socket initialization
		Properties prop = KappaProperties.getInstance();
		//System.out.println(prop.getProperty());
		//final Socket connection = new Socket(prop.getProperty("SERVER_IP"), Integer.parseInt(prop.getProperty("SERVER_PORT")));
		final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// Cleanup planning  
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		
		 
		
	 
		
		chartPanel = createChartPanel(socket,args);
		add(chartPanel, BorderLayout.CENTER);
 
		
		final JComboBox<SimulationIdentifier> cbScenChoice = new JComboBox<SimulationIdentifier>();
		cbScenChoice.setToolTipText("Veuillez choisir la simulation");
		cbScenChoice.setEditable(true);
		cbScenChoice.setFont(new Font("Tahoma", Font.PLAIN, 12)); 
		cbScenChoice.setSelectedItem("- Choisir -");  		
		setSize(1240, 680);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//Button to print the graph
		JPanel pnBtn = new JPanel();
		add(pnBtn, BorderLayout.NORTH);
		JButton btnPrint = new JButton("Imprimer");
		pnBtn.add(btnPrint);
		// Sending the account_id over to the server
		
		 
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		 
					 printChart(); 
			}
		});
		
		GetAllSimsQuery query = new GetAllSimsQuery("-1");
		out.println(query.toString());
		// Receiving the server's response
		String message = in.readLine();
		//System.out.print(message);
		//Treating the server's response
		try {
			// Prefix and content detection
			int prefixEnd = message.indexOf(' ');
			
			if(prefixEnd == -1) {
				throw new Exception("No prefix");
			}
			
			String prefix = message.substring(0, prefixEnd);
			String content = message.substring(prefixEnd + 1);
			
			//System.out.print(message);
			// Prefix identification
			switch(prefix) {
			case "ERR":
				JOptionPane.showMessageDialog(thisObject, "Format error. Try downloading the newest version.");
				break;
			
			case "OK":
				// De-serialization
				GetAllSimsServerResponse response = JsonImpl.fromJson(content, GetAllSimsServerResponse.class);     
				//System.out.println(response); 
				List<SimulationIdentifier> listSims=  response.getSimulations();  
				for(int i=0; i<listSims.toArray().length;i++){
				//System.out.println(listSims.toArray()[i]);
				cbScenChoice.addItem((SimulationIdentifier) listSims.toArray()[i]); 
			    } 
				//System.out.println(listSims.toArray().length); 
				 
 				//System.out.println(theList);
				//for(int i=0; i<=theList.length;i++){
					//System.out.println(theList[i]);
				//}
				//cbScenChoice.setModel(response);
				break;
			
			default:
				throw new Exception("Unknown prefix");
			}
		} catch (Exception e1) {
			//System.out.print(e1); 
			JOptionPane.showMessageDialog(thisObject, "Unknown response format. Please try again later or download the newest version.");
		} 
		
		//Action on select item in combox list of scenario
				cbScenChoice.addActionListener(new ActionListener(){ 
					@Override
					public void actionPerformed(ActionEvent arg) {
						// TODO Auto-generated method stub
						EventQueue.invokeLater(new Runnable() { // Starting a thread is long, so we need to clear the eventqueue first
							public void run() {
								new Thread(new Runnable() { // We launch a new thread for this treatment, so that the GUI can still update. This new thread will be the host for the onSuccessfulLogin callable 
									public void run() { 
											try {
												createChartPanel(socket, ((SimulationIdentifier) cbScenChoice.getSelectedItem()).getId());
											} catch (NumberFormatException | IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
									}
								}).start();
							}
						});
					}
					
				});
		
	}
	
	
	
	private JPanel createChartPanel(Socket socket, String args) throws NumberFormatException, UnknownHostException, IOException {
		//System.out.print(args);
		 
		String chartTitle = "Graphiques de l'évolution sur la durée du prêt - N°"+args;
		String xAxisLabel = "ECHEANCES";
		String yAxisLabel = "MONTANT";
		
		XYDataset dataset = createDataset(socket, args.toString());
		
//		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
	//			xAxisLabel, yAxisLabel, dataset);
		
//		boolean showLegend = false;
//		boolean createURL = false;
//		boolean createTooltip = false;
//		
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
				xAxisLabel, yAxisLabel, dataset, 
				PlotOrientation.VERTICAL, true, true, false);
		
		customizeChart(chart);
		
		// saves the chart as an image files
		File imageFile = new File("XYLineChart"+args+".png");
		int width = 940;
		int height = 705;
		
		try {
			ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
		} catch (IOException ex) {
			System.err.println(ex);
		}
		
		return new ChartPanel(chart);
	}

	private XYDataset createDataset(Socket socket, String args) throws NumberFormatException, UnknownHostException, IOException { 
		
		Properties prop = KappaProperties.getInstance();
		//final Socket connection = new Socket("localhost", Integer.parseInt(prop.getProperty("SERVER_PORT")));
		final PrintWriter  out = new PrintWriter(socket.getOutputStream(), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// TODO: check if on successful login, when the auth window gets disposed, this listener is called
	 
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Capital");
		XYSeries series2 = new XYSeries("Assurance");
		XYSeries series3 = new XYSeries("Intérêts");  

		  
		 

		try {
			// Sending the loan_id over to the server

			String id = args.toString();
			GetSimQuery query = new GetSimQuery(id);
			 
			out.println(query.toString());
			// Receiving the server's response
			String message = in.readLine(); 

			//Treating the server's response
			try {
				// Prefix and content detection
				int prefixEnd = message.indexOf(' ');
				
				if(prefixEnd == -1) {
					throw new Exception("No prefix");
				}
				
				String prefix = message.substring(0, prefixEnd);
				String content = message.substring(prefixEnd + 1);
				
				// Prefix identification
				switch(prefix) {
				case "ERR":
					System.out.println("Format error. Try downloading the newest version.");
					 
					break;
				
				case "OK":
					// De-serialization
					Simulation response = JsonImpl.fromJson(content, Simulation.class);  
					List<Repayment> listrepay=  response.getRepayments();    
					
					//we prepare to bind data into our JTable
					for(int i=0 ; i < listrepay.size() ; i++) { 
						series1.add(i+1, listrepay.get(i).getCapital());
						series2.add(i+1, listrepay.get(i).getInsurance());
						series3.add(i+1, listrepay.get(i).getInterest()); 
					}    
					
					dataset.addSeries(series1);
					dataset.addSeries(series2);
					dataset.addSeries(series3);
					break;

				default:
					throw new Exception("Unknown prefix");
				}
			} catch (Exception e1) {
				
				System.out.println(e1);
			}
		} catch (IOException e1) {
			System.out.println("Unable to connect to the server. Please try again later.");
		}  
		return dataset;
	}
	
	private void customizeChart(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);

		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f));
		
		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		
		// sets renderer for lines
		plot.setRenderer(renderer);
		
		// sets plot background
		plot.setBackgroundPaint(Color.DARK_GRAY);
		
		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		
	}
	
	private void printChart(){  
		Toolkit tkp = chartPanel.getToolkit();
	    PrintJob pjp = tkp.getPrintJob(this, null, null);
	    Graphics g = pjp.getGraphics();
	    chartPanel.print(g);
	    g.dispose();
	    pjp.end();
	}

	public static void main(final Socket socket, final String args) throws IOException {
		 

		/* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
        	
            @Override
            public void run() {
            	ChartResult cr = null;
				try {
					try {
						cr = new ChartResult(socket, args);
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cr.setVisible(true);
                
            }
        });
	}
}