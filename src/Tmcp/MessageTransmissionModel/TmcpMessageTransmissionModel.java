package projects.Tmcp.MessageTransmissionModel;

import jsensor.nodes.Node;
import jsensor.nodes.messages.Message;
import jsensor.nodes.models.MessageTransmissionModel;

public class TmcpMessageTransmissionModel extends MessageTransmissionModel {

	@Override
	public float timeToReach(Node n1, Node n2, Message m) {
		return 10;
	}

}
