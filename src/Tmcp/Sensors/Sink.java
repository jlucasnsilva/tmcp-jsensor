package projects.Tmcp.Sensors;

import java.util.HashSet;
import java.util.Set;

import jsensor.nodes.Node;
import jsensor.nodes.messages.Inbox;
import jsensor.nodes.messages.Message;
import projects.Tmcp.Messages.ETmcpMessageType;
import projects.Tmcp.Messages.TmcpMessage;
import projects.Tmcp.Timers.SensorTimer;

public class Sink extends Node implements IEventProcessor {

	public static final int NUM_OF_CHANNELS = 6;

	private final int SINK_LEVEL = 0;

	private int[] channelsInterference;

	private Set<Integer> hasChan;

	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasMoreMessages()) {
			Message message = inbox.getNextMessage();
			
			if (message instanceof TmcpMessage) {
				TmcpMessage m = (TmcpMessage) message;
				
				if (m.type == ETmcpMessageType.CHANNEL_REQUEST && !hasChan.contains(m.senderID)) {
					int chan = 0;
					
					for (int i = 1; i < NUM_OF_CHANNELS; i++) {
						if (channelsInterference[i] < channelsInterference[chan]) {
							chan = i;
						}
					}
					
					channelsInterference[chan]++;
					
					TmcpMessage.AssignedChannel ac = new TmcpMessage.AssignedChannel(getID(), chan, channelsInterference[chan]);
					respond(m, ETmcpMessageType.ASSIGNED_CHANNEL, ac);
					
					// as nodes will request for a channel by sending a channel request
					// through all channel frequency (in case a parent has had a channel
					// other than the default allocated) the sink has to check if it has
					// already provided a channel for a node, for the sink can listen in
					// all channels.
					hasChan.add(m.senderID);
				} else if (m.type == ETmcpMessageType.JOB) {
					
					Sink.jobMessageReceived();
					
				}
			}
		} // end of the while loop
	} // end of the method 

	@Override
	public void onCreation() {
		this.channelsInterference = new int[NUM_OF_CHANNELS];
		this.hasChan = new HashSet<>();
		
		SensorTimer tst = new SensorTimer();
		tst.startRelative(1, this);
	}

	public void respond(TmcpMessage m, ETmcpMessageType messageType) {
		respond(m, messageType, null);
	}

	public void respond(TmcpMessage m, ETmcpMessageType messageType, TmcpMessage.AssignedChannel ac) {
		TmcpMessage newMessage = new TmcpMessage(this.getID(), SINK_LEVEL, m.senderID, m.outChannel, m.inChannel, messageType, ac);
		
		this.multicast(newMessage);
	}

	public void process() {
		// the only event action is to start the fat tree constructions
		int chan = 0;
		int irrelevantID = 0;
		TmcpMessage newMessage = new TmcpMessage(this.getID(), SINK_LEVEL, irrelevantID, chan, chan, ETmcpMessageType.BUILD_FAT_TREE);
		
		multicast(newMessage);
	}

	// ------------------------------------------

	private static int jobMessagesReceived = 0;

	public static void jobMessageReceived() {
		jobMessagesReceived++;
	}

	public static int getJobMessagesReceived() {
		return jobMessagesReceived;
	}
}
