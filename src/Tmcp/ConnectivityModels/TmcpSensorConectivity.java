package projects.Tmcp.ConnectivityModels;

import jsensor.nodes.Node;
import jsensor.nodes.models.ConnectivityModel;

public class TmcpSensorConectivity extends ConnectivityModel {

	@Override
	public boolean isConnected(Node n, Node m) {
		return true;
	}

}
