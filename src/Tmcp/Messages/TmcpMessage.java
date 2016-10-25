package projects.Tmcp.Messages;

import jsensor.nodes.messages.Message;

public class TmcpMessage extends Message {

	public final int senderID;

	public final int senderLevel;

	public final int destID;

	public final int outChannel;

	public final int inChannel;

	public final ETmcpMessageType type;

	public final AssignedChannel assignedChannel;

	public TmcpMessage(int senderID, int senderLevel, int destID, int inChannel, int outChannel, ETmcpMessageType type) {
		this(senderID, senderLevel, destID, inChannel, outChannel, type, null);
	}

	public TmcpMessage(int senderID, int senderLevel, int destID, int inChannel, int outChannel, ETmcpMessageType type, AssignedChannel assignedChannel) {
		this.senderID = senderID;
		this.senderLevel = senderLevel;
		this.destID = destID;
		this.outChannel = outChannel;
		this.inChannel = inChannel;
		this.type = type;
		this.assignedChannel = assignedChannel;
	}

	@Override
	public Message clone() {
		return new TmcpMessage(senderID, senderLevel, destID, outChannel, inChannel, type, assignedChannel);
	}

	public static class AssignedChannel {
		public final int parentID;
		public final int channel;
		public final int interference;
		public AssignedChannel(int parentID, int channel, int interference) {
			this.parentID = parentID;
			this.channel = channel;
			this.interference = interference;
		}
		@Override
		public String toString() {
			return "[parentID: " + parentID + ", channel: " + channel + ", interference: " + interference + "]";
		}
	}
	
	@Override
	public String toString() {
		return  "{\n" +
				"\tfrom: " + senderID + "\n" +
				"\tto: " + destID + "\n" +
				"\tIO: " + inChannel + "/" + outChannel + "\n" +
				"\ttype: " + type + "\n" +
				"\tassigned channel: " + assignedChannel +
				"\n}\n";
	}

	// ============================================================================
	// ============================================================================
	// ============================================================================
	// ============================================================================

	private static int controlMessageCount = 0;

	private static int jobMessageCount = 0;

	public static synchronized void controlMessageSent() {
		controlMessageCount++;
	}

	public static synchronized void jobMessageSent() {
		jobMessageCount++;
	}
	
	public static int getJobMessageCount() {
		return jobMessageCount;
	}

	public static int getControlMessageCount() {
		return controlMessageCount;
	}
}
