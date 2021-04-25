package sua.autonomouscar.simulation.console.commands;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.controller.Controller;
import sua.autonomouscar.controller.SimulationContoller;
import sua.autonomouscar.controller.monitors.road.RoadContextMonitor;
import sua.autonomouscar.controller.probes.road.RoadStatusProbe;
import sua.autonomouscar.controller.probes.road.RoadTypeProbe;
import sua.autonomouscar.infrastructure.OSGiUtils;

public class SimulationContollerConfigurator {
	public static void configure(BundleContext context) {
		SimulationContoller controller = (SimulationContoller)OSGiUtils.getService(context, Controller.class);
		
		// Probes.
		RoadStatusProbe roadStatusProbe = new RoadStatusProbe(context);
		roadStatusProbe.setRoadSensor("RoadSensor");
		
		RoadTypeProbe roadTypeProbe = new RoadTypeProbe(context);
		roadTypeProbe.setRoadSensor("RoadSensor");
		
		// Monitors.
		controller.addMonitor(new RoadContextMonitor(context, roadStatusProbe, roadTypeProbe));
	}
}