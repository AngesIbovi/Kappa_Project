package elements;


import java.util.List;

import javax.swing.JPanel;

import model.response.GetSimServerResponse;

/**
 * Abstract class for the various elements viewed in the SimulationComparisonGUI.
 * @author Kappa-V
 * @version R3 sprint 3 - 16/05/2016
 */
@SuppressWarnings("serial")
public abstract class SimulationComparisonElement extends JPanel {
	public abstract void setSimulations(List<GetSimServerResponse> simulations);
}
