package org.view_print_results_loan;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import model.query.GetSimQuery;
import model.simulation.Simulation;
import model.simulation.Repayment;
import util.JsonImpl;
import util.KappaProperties;

/**
* We create a bar chart.
*/
public class BarChartResult extends JFrame {

   /**
    * Creates a new demo instance.
    *
    * @param title  the frame title.
 * @throws IOException 
 * @throws UnknownHostException 
 * @throws NumberFormatException 
    */
   public BarChartResult(final Socket socket, String args,String title) throws NumberFormatException, UnknownHostException, IOException {
       super(title);
       CategoryDataset dataset = createDataset(socket, args);
       JFreeChart chart = createChart(dataset);
       ChartPanel chartPanel = new ChartPanel(chart, false);
       chartPanel.setPreferredSize(new Dimension(1240, 680));
       setSize(1240, 680);
       setContentPane(chartPanel);
		// Cleanup planning  
       setDefaultCloseOperation(ApplicationFrame.DISPOSE_ON_CLOSE);
 
   }

   /**
    * Returns a sample dataset.
    * 
    * @return The dataset.
 * @throws IOException 
 * @throws UnknownHostException 
 * @throws NumberFormatException 
    */
   private static CategoryDataset createDataset(Socket socket,String args) throws NumberFormatException, UnknownHostException, IOException {
       
       // row keys...
       String series1 = "Capital";
       String series2 = "Assurance";
       String series3 = "Intérêts";
       String series4 = "Restant dû";

       // column keys... 

      // create the dataset...
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
  
 
		final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// TODO: check if on successful login, when the auth window gets disposed, this listener is called
		 
		 
		
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
					//String[][] datas = (String[][]) new String[listrepay.size()][6];
					float restant=0;
					//we prepare to bind data into our JTable
					for(int i=0 ; i < listrepay.size() ; i++) { 
						restant=restant+listrepay.get(i).getCapital();
						dataset.addValue(listrepay.get(i).getCapital(), series1, Float.toString(i+1));
						dataset.addValue(listrepay.get(i).getInsurance(), series2, Float.toString(i+1));
						dataset.addValue(listrepay.get(i).getInterest(), series3, Float.toString(i+1));
						dataset.addValue(response.getCapital()-restant, series4, Float.toString(i+1)); 
					}    
					 
					break;

				default:
					throw new Exception("Unknown prefix");
				}
			} catch (Exception e1) {
				System.out.println("Unknown response format. Please try again later or download the newest version.");
			}
		} catch (IOException e1) {
			System.out.println("Unable to connect to the server. Please try again later.");
		}  
      
      
      
      return dataset;
      
  }
  
  /**
   * Creates a sample chart.
   * 
   * @param dataset  the dataset.
  * 
    * @return The chart.
   */
 private static JFreeChart createChart(CategoryDataset dataset) {
     
       // create the chart...
     JFreeChart chart = ChartFactory.createBarChart(
         "Graphique des résultats - MONTANT RESTANT DÛ",       // chart title
         "Echéance",               // domain axis label
           "Montant",                  // range axis label
        dataset,                  // data
         PlotOrientation.VERTICAL, // orientation
          true,                     // include legend
			  true,                     // tooltips?
          false                     // URLs?
    );

     // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

    // set the background color for the chart...
       chart.setBackgroundPaint(Color.white);

      // get a reference to the plot for further customisation...
      CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.lightGray);
     plot.setDomainGridlinePaint(Color.white);
     plot.setDomainGridlinesVisible(true);
     plot.setRangeGridlinePaint(Color.white);

     // ******************************************************************
     //  More than 150 demo applications are included with the JFreeChart
      //  Developer Guide...for more information, see:
     //
    //  >   http://www.object-refinery.com/jfreechart/guide.html
     //
     // ******************************************************************
      
      // set the range axis to display integers only...
     final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
     rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

     // disable bar outlines...
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setDrawBarOutline(false);
      
     // set up gradient paints for series...
    GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, new Color(0, 0, 64));
    GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, new Color(0, 64, 0));
     GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, new Color(64, 0, 0));
      renderer.setSeriesPaint(0, gp0);
     renderer.setSeriesPaint(1, gp1);
     renderer.setSeriesPaint(2, gp2);

     CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(
           CategoryLabelPositions.createUpRotationLabelPositions(
                    Math.PI / 6.0));
     // OPTIONAL CUSTOMISATION COMPLETED.
     
    return chart;
    
 }
  
 /**
  * Starting point for the demonstration application.
  *
   * @param args  ignored.
 * @throws IOException 
  */
 public static void main(Socket socket, final String args) throws IOException {
	 try {
			KappaProperties.init();
			JsonImpl.init();
		} catch(IOException e) {
			System.out.print("Exiting Client.");
			throw e;
		}
     BarChartResult demo = null;
	try {
		demo = new BarChartResult(socket,args,"Dashboard");
	} catch (NumberFormatException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     demo.pack();
     RefineryUtilities.centerFrameOnScreen(demo);
     demo.setVisible(true);
}

}
