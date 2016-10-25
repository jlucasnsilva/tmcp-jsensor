package projects.Tmcp.Timers;

import jsensor.nodes.events.TimerEvent;
import projects.Tmcp.Sensors.IEventProcessor;
import projects.Tmcp.Sensors.Sensor;
import projects.Tmcp.Sensors.Sink;

public class SensorTimer extends TimerEvent {

	@Override
	public void fire() {
		IEventProcessor s;
		
		if (this.node instanceof Sensor) {
			s = (Sensor) this.node;
		} else {
			s = (Sink) this.node;
		}
		
		s.process();
	}

}
