package projects.Tmcp.Sensors;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import jsensor.nodes.Node;
import jsensor.nodes.messages.Inbox;
import jsensor.nodes.messages.Message;
import projects.Tmcp.Messages.ETmcpMessageType;
import projects.Tmcp.Messages.TmcpMessage;
import projects.Tmcp.Timers.SensorTimer;

public class Sensor extends Node implements IEventProcessor {

	private final int WORKING_NODES_PROPORTION = 2;

	private final int RELATIVE_TIME = 1;

	private final int MAX_NUM_OF_MESSAGES = 10;

	/**
	 * ID of the chosen parent.
	 */
	private int parentID;

	/**
	 * Level/depth of this node in the tree.
	 */
	private int level;

	/**
	 * Channel for sending messages.
	 */
	private int outChannel;

	/**
	 * Channel for listening for messages.
	 */
	private int inChannel;
	
	/**
	 * Channel interference.
	 */
	private int channelInterference;

	/**
	 * Number of message still to be sent.
	 */
	private int messageCount;

	// ------------------------------------------------------------------------

	/**
	 * Next action to be taken;
	 */
	private EAction action;

	// ------------------------------------------------------------------------

	/**
	 * Found channel.
	 */
	private boolean foundChannel;

	/**
	 * Finished build tree step.
	 */
	private boolean treeBuilt;

	// ------------------------------------------------------------------------

	/**
	 * This node's parents.
	 */
	private Set<Integer> parents;

	/**
	 * Channels to choose from.
	 */
	private Map<Integer, TmcpMessage.AssignedChannel> channelChoices;

	// ------------------------------------------------------------------------

	/**
	 * Messages kept to be processed in the future.
	 */
	private Deque<TmcpMessage> deferQueue;

	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasMoreMessages()) {
			Message message = inbox.getNextMessage();
			
			if (message instanceof TmcpMessage) {
				handleMessage((TmcpMessage) message);
			}	
		}
		
		if (this.action == EAction.REQUEST_CHANNEL || this.action == EAction.CHOOSE_CHANNEL) {
			process();
		}
		
		resolvePending();
	}

	private void resolvePending() {
		// respond the REQUEST_CHANNEL messages that were saved (see handleRequestChannel)
		if (this.foundChannel && deferQueue.size() > 0) {
			// the answer is sent through the default channel 0.
			TmcpMessage.AssignedChannel ac = new TmcpMessage.AssignedChannel(getID(), this.inChannel, this.channelInterference);
			
			// respond the saved messages.
			for (TmcpMessage msg : deferQueue) {
				transmit(msg.senderID, this.inChannel, 0, ETmcpMessageType.ASSIGNED_CHANNEL, ac);
				
				////////////////////////////////
				////////////////////////////////
				TmcpMessage.controlMessageSent();
				////////////////////////////////
				////////////////////////////////
			}
			
			// empty the queue
			this.deferQueue = new LinkedList<>();
		}
	}

	private void handleMessage(TmcpMessage m) {
		if (m.outChannel == this.inChannel)
		{
			if (m.destID == getID())
			{
				if (this.level < m.senderLevel)
				{
					if (m.type == ETmcpMessageType.JOB) {
						handleJob(m);
					} else if (m.type == ETmcpMessageType.CHANNEL_SELECTED) {
						handleChannelSelected(m);
					}
				} else if (this.level > m.senderLevel && m.type == ETmcpMessageType.ASSIGNED_CHANNEL && !this.foundChannel) {
					handleAssignedChannel(m);
				}
			}
			
			if (m.type == ETmcpMessageType.CHANNEL_REQUEST && this.level < m.senderLevel) {
				handleChannelRequest(m);
			}
			
			if (m.type == ETmcpMessageType.BUILD_FAT_TREE && this.level > m.senderLevel) {
				handleBuildFatTree(m);
			}
		}
	}

	private void handleJob(TmcpMessage m) {
		// when a node receives a JOB message, it retransmits it to
		// its parent. All JOB messages target the sink.
		retransmit(m);
		
		////////////////////////////////
		////////////////////////////////
		Sensor.signalRetransmission();
		////////////////////////////////
		////////////////////////////////
	}

	private void handleChannelSelected(TmcpMessage m) {
		// this message is received when a child chooses this node's
		// channel.
		this.channelInterference++;
	}

	private void handleAssignedChannel(TmcpMessage m) {
		this.channelChoices.put(m.senderID, m.assignedChannel);
		this.action = EAction.CHOOSE_CHANNEL;
	}

	private void handleChannelRequest(TmcpMessage m) {
		// if this sensor doesn't yet have a channel allocated (still working
		// on  the default  channel 0), the  request is  postponed for  later
		// processing.
		if (!foundChannel) {
			deferQueue.push(m);
			return;
		}
		
		TmcpMessage.AssignedChannel ac = new TmcpMessage.AssignedChannel(getID(), this.inChannel, this.channelInterference);
		// the answer is sent through the default channel 0.
		transmit(m.senderID, this.inChannel, 0, ETmcpMessageType.ASSIGNED_CHANNEL, ac);
		
		resolvePending();
		
		////////////////////////////////
		////////////////////////////////
		TmcpMessage.controlMessageSent();
		////////////////////////////////
		////////////////////////////////
	}

	private void handleBuildFatTree(TmcpMessage m) {
		this.parents.add( m.senderID );
		this.level = m.senderLevel + 1;
		
		if (!this.treeBuilt) {
			// destID is irrelevant for this kind of message.
			transmit(0, 0, 0, ETmcpMessageType.BUILD_FAT_TREE);
			
			//////////////////////////////////
			//////////////////////////////////
			TmcpMessage.controlMessageSent();
			Sensor.signalInFatTree();
			//////////////////////////////////
			//////////////////////////////////
			
			this.action = EAction.REQUEST_CHANNEL;
			this.treeBuilt = true;
		}
	}

	@Override
	public void onCreation() {
		this.level = Integer.MAX_VALUE;
		this.inChannel = 0;
		this.outChannel = 0;
		this.parentID = 0;
		// this.action = EAction.FIND_SINK;
		this.action = EAction.WAIT_FOR_PARENT;
		this.parents = new HashSet<>();
		this.foundChannel = false;
		this.treeBuilt = false;
		this.deferQueue = new LinkedList<>();
		this.channelChoices = new HashMap<>();
		this.channelInterference = 0;
		this.messageCount = 0;
	}

	public void process() {
		if (this.action == EAction.WORK && this.foundChannel && getID() % WORKING_NODES_PROPORTION == 0) {
			transmit(this.parentID, this.inChannel, this.outChannel, ETmcpMessageType.JOB);
			
			////////////////////////////////
			////////////////////////////////
			TmcpMessage.jobMessageSent();
			////////////////////////////////
			////////////////////////////////
			
			this.messageCount++;
			if (this.messageCount < MAX_NUM_OF_MESSAGES) {
				createEvent();
			}
		} else if (this.action == EAction.REQUEST_CHANNEL) {
			// a channel request channel is sent through all channels in
			// case the parents have already chosen other channel the
			// default.
			for (int chan = 0; chan < Sink.NUM_OF_CHANNELS; chan++) {
				// the destID is irrelevant
				// all parents will receive it
				transmit(0, 0, chan, ETmcpMessageType.CHANNEL_REQUEST);

				//////////////////////////////////
				//////////////////////////////////
				TmcpMessage.controlMessageSent();
				//////////////////////////////////
				//////////////////////////////////
			}
			
			this.action = EAction.CHOOSE_CHANNEL;
		} else if (this.action == EAction.CHOOSE_CHANNEL) {
			// when this node has received the channel id of all of its parents,
			// it will choose the best amongst them.
			// TODO if (this.channelChoices.size() == this.parents.size() && this.parents.size() > 0) {
			// using: at least half of the parents sent their channels
			if (this.channelChoices.size() >= (this.parents.size() / 2) && this.parents.size() > 0 && this.channelChoices.size() > 0) {
				TmcpMessage.AssignedChannel f = channelChoices.values().iterator().next();
				int chan = f.channel;
				int parentID = f.parentID;
				int interference = f.interference;
				
				for (TmcpMessage.AssignedChannel ac : channelChoices.values()) {
					if (ac.interference < interference) {
						chan = ac.channel;
						parentID = ac.parentID;
						interference = ac.interference;
					}
				}
				
				this.parentID = parentID;
				this.inChannel = chan;
				this.outChannel = chan;
				this.channelInterference = interference + 1;
				
				// subscribe to one parent.
				transmit(parentID, chan, chan, ETmcpMessageType.CHANNEL_SELECTED);
				this.foundChannel = true;
				
				////////////////////////////////
				////////////////////////////////
				TmcpMessage.controlMessageSent();
				
				Sensor.signalFoundChannel();
				////////////////////////////////
				////////////////////////////////
				
				resolvePending();
				
				this.action = EAction.WORK;
				
				// -----------------------------------------
				createEvent();
			}
		}
	}

	private void retransmit(TmcpMessage m) {
		this.multicast( new TmcpMessage(m.senderID, m.senderLevel, this.parentID, m.inChannel, m.outChannel, m.type, m.assignedChannel) );
	}

	private void transmit(int destID, int inChan, int outChan, ETmcpMessageType messageType) {
		transmit(destID, inChan, outChan, messageType, null);
	}
	
	private void transmit(int destID, int inChan, int outChan, ETmcpMessageType messageType, TmcpMessage.AssignedChannel ac) {
		this.multicast( new TmcpMessage(this.getID(), this.level, destID, inChan, outChan, messageType, ac) );
	}
	
	private void createEvent() {
		createEvent(RELATIVE_TIME);
	}
	
	private void createEvent(int time) {
		SensorTimer tst = new SensorTimer();
		tst.startRelative(time, this);
	}

	// -----------------------------------------------------

	private static int numberOfSensorsThatFoundAChannel = 0;

	private static int numberOfSensorsInTheFatTree = 0;

	private static int numberOfRetransmissions = 0;

	public static synchronized void signalRetransmission() {
		numberOfRetransmissions++;
	}

	public static int getNumberOfRetransmissions() {
		return numberOfRetransmissions;
	}

	public static synchronized void signalInFatTree() {
		numberOfSensorsInTheFatTree++;
	}

	public static int getNumberOfSensorsInTheFatTree() {
		return numberOfSensorsInTheFatTree;
	}

	public static synchronized void signalFoundChannel() {
		numberOfSensorsThatFoundAChannel++;
	}

	public static int getNumberOfSensorThatFoundAChannel() {
		return numberOfSensorsThatFoundAChannel;
	}

}
