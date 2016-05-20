package model.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication class. See the protocol's documentation for more details.
 * @version R3 sprint 3 - 13/05/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-Implemented toString for the SimulationIdentifier subclass</br>
 */
public class GetSimsServerResponse extends ServerResponse {
	// Inner class
	public static class SimulationIdentifier {
		private String name;
		private String id;
		
		public SimulationIdentifier(String name, String id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	// Parameters
	
	private List<SimulationIdentifier> simulations;

	
	// For server-side deserialization
	
	public GetSimsServerResponse(List<SimulationIdentifier> simulations) {
		super();
		this.simulations = simulations;
	}

	public List<SimulationIdentifier> getSimulations() {
		return simulations;
	}

	public void setSimulations(List<SimulationIdentifier> simulations) {
		this.simulations = simulations;
	}
	
	
	// For client-side easier creation
	
	public GetSimsServerResponse() {
		super();
		this.simulations = new ArrayList<>();
	}
	
	public void addSimulation(SimulationIdentifier simulation) {
		this.simulations.add(simulation);
	}
}
