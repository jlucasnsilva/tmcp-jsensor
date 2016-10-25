package projects.Tmcp;

import jsensor.runtime.AbsCustomGlobal;
import jsensor.runtime.Jsensor;
import projects.Tmcp.Messages.TmcpMessage;
import projects.Tmcp.Sensors.Sensor;
import projects.Tmcp.Sensors.Sink;

public class CustomGlobal extends AbsCustomGlobal {

	@Override
	public boolean hasTerminated() {
		return false;
	}

	@Override
	public void postRound() {
		/*
		System.out.println("==========================================================");
		System.out.println("Number of nodes: " + Jsensor.numNodes);
		System.out.println("Number of channels available: " + Sink.NUM_OF_CHANNELS);
		System.out.println("Number of sensor that joined the fat tree: " + Sensor.getNumberOfSensorsInTheFatTree());
		System.out.println("Number of sensors that found a channel: " + Sensor.getNumberOfSensorThatFoundAChannel());
		System.out.println("Number of control messages sent: " + TmcpMessage.getControlMessageCount());
		System.out.println("Number of sensor messages sent: " + TmcpMessage.getJobMessageCount());
		System.out.println("Number of sensor messages received by the sink: " + Sink.getJobMessagesReceived());
		System.out.println("Number of retransmissions: " + Sensor.getNumberOfRetransmissions());
		*/
		
		/*
		Jsensor.log("==========================================================");
		Jsensor.log("Number of nodes: " + Jsensor.numNodes);
		Jsensor.log("Number of channels available: " + Sink.NUM_OF_CHANNELS);
		Jsensor.log("Number of sensor that joined the fat tree: " + Sensor.getNumberOfSensorsInTheFatTree());
		Jsensor.log("Number of sensors that found a channel: " + Sensor.getNumberOfSensorThatFoundAChannel());
		Jsensor.log("Number of control messages sent: " + TmcpMessage.getControlMessageCount());
		Jsensor.log("Number of sensor messages sent: " + TmcpMessage.getJobMessageCount());
		Jsensor.log("Number of sensor messages received by the sink: " + Sink.getJobMessagesReceived());
		Jsensor.log("Number of retransmissions: " + Sensor.getNumberOfRetransmissions() + "\n");
		*/
		
		Jsensor.log("Number of Nodes, Number of Channels, Number of Nodes that Joined the Fat-tree, Number of Sensors that Found a Channel, Number of Control Messages, Number of App Messages, Number of App Messages Received by the Sink, Number of Retransmissions");
		Jsensor.log("" + Jsensor.numNodes +
					", " + Sink.NUM_OF_CHANNELS +
					", " + Sensor.getNumberOfSensorsInTheFatTree() +
					", " + Sensor.getNumberOfSensorThatFoundAChannel() +
					", " + TmcpMessage.getControlMessageCount() +
					", " + TmcpMessage.getJobMessageCount() +
					", " + Sink.getJobMessagesReceived() +
					", " + Sensor.getNumberOfRetransmissions());
	}

	@Override
	public void postRun() {
		/* TODO not working
		Jsensor.log("Number of nodes: " + Jsensor.numNodes);
		Jsensor.log("Number of channels available: " + Sink.NUM_OF_CHANNELS);
		Jsensor.log("Number of sensor that joined the fat tree: " + Sensor.getNumberOfSensorsInTheFatTree());
		Jsensor.log("Number of sensors that found a channel: " + Sensor.getNumberOfSensorThatFoundAChannel());
		Jsensor.log("Number of control messages sent: " + TmcpMessage.getControlMessageCount());
		Jsensor.log("Number of sensor messages sent: " + TmcpMessage.getJobMessageCount());
		Jsensor.log("Number of sensor messages received by the sink: " + Sink.getJobMessagesReceived());
		Jsensor.log("Number of retransmissions: " + Sensor.getNumberOfRetransmissions());
		*/
	}

	@Override
	public void preRound() {
	}

	@Override
	public void preRun() {
	}

}
