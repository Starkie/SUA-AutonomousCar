package sua.autonomouscar.controller.rules;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.controller.properties.car.CurrentDrivingServiceStatus;
import sua.autonomouscar.controller.properties.car.DistanceSensorHealthStatus;
import sua.autonomouscar.controller.properties.car.EngineHealthStatus;
import sua.autonomouscar.controller.properties.car.FallbackPlanHealthStatus;
import sua.autonomouscar.controller.properties.car.HumanSensorsHealthStatus;
import sua.autonomouscar.controller.properties.car.LineSensorsHealthStatus;
import sua.autonomouscar.controller.properties.car.NotificationServiceHealthStatus;
import sua.autonomouscar.controller.properties.driver.DriverContext;
import sua.autonomouscar.controller.properties.road.RoadContext;
import sua.autonomouscar.controller.rules.autonomy.L0.SwitchToL0ManualDrivingFromL1Rule;
import sua.autonomouscar.controller.rules.autonomy.L0.SwitchToL0ManualDrivingFromL2AdaptiveCruiseControlRule;
import sua.autonomouscar.controller.rules.autonomy.L0.SwitchToL0ManualDrivingFromL2LaneKeepingAssistRule;
import sua.autonomouscar.controller.rules.autonomy.L0.SwitchToL0ManualDrivingFromL3Rule;
import sua.autonomouscar.controller.rules.autonomy.L1.SwitchToL1AssistedDrivingFromL0Rule;
import sua.autonomouscar.controller.rules.autonomy.L1.SwitchToL1AssistedDrivingFromL3Rule;
import sua.autonomouscar.controller.rules.autonomy.L2.SwitchToL2AdaptiveCruiseControlFromL1Rule;
import sua.autonomouscar.controller.rules.autonomy.L2.SwitchToL2AdaptiveCruiseControlFromL2Rule;
import sua.autonomouscar.controller.rules.autonomy.L2.SwitchToL2LaneKeepingAssistFromL1;
import sua.autonomouscar.controller.rules.autonomy.L2.SwitchToL2LaneKeepingAssistFromL2;
import sua.autonomouscar.controller.rules.autonomy.L2.SwitchToL2LaneKeepingAssistFromL3;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3CityChaufferFromL2Rule;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3CityChaufferFromL3Rule;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3HighwayChaufferFromL2Rule;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3HighwayChaufferFromL3Rule;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3TrafficJamChaufferFromL2;
import sua.autonomouscar.controller.rules.autonomy.L3.SwitchToL3TrafficJamChaufferFromL3;
import sua.autonomouscar.controller.rules.configuration.ReplaceFrontDistanceSensorRule;
import sua.autonomouscar.controller.rules.configuration.ReplaceLeftDistanceSensorRule;
import sua.autonomouscar.controller.rules.configuration.ReplaceRearDistanceSensorRule;
import sua.autonomouscar.controller.rules.configuration.ReplaceRightDistanceSensorRule;
import sua.autonomouscar.controller.rules.notification.steeringwheel.DisableSteeringWheelHapticVibrationRule;
import sua.autonomouscar.controller.rules.notification.steeringwheel.EnableSteeringWheelHapticVibrationRule;
import sua.autonomouscar.infrastructure.devices.Steering;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private EnableNotificationsInL1Rule enableNotificationsInL1Rule;
	private SwitchToL0ManualDrivingFromL1Rule switchToL0ManualDrivingFromL1;
	private SwitchToL1AssistedDrivingFromL0Rule switchToL1AssistedDrivingFromL0Rule;
	private SwitchToL1AssistedDrivingFromL3Rule switchToL1AssistedDrivingFromL3Rule;
    private SwitchToL2AdaptiveCruiseControlFromL1Rule switchToL2AdaptiveCruiseControlFromL1Rule;
    private SwitchToL2LaneKeepingAssistFromL1 switchToL2LaneKeepingAssistFromL1Rule;
    private SwitchToL2LaneKeepingAssistFromL3 switchToL2LaneKeepingAssistFromL3Rule;
    private SwitchToL3CityChaufferFromL2Rule switchToL3CityChaufferFromL2Rule;
    private SwitchToL3HighwayChaufferFromL2Rule switchToL3HighwayChaufferFromL2Rule;
    private SwitchToL3TrafficJamChaufferFromL2 swithToL3TrafficJamChaufferFromL2;
    private SwitchToL3TrafficJamChaufferFromL3 swithToL3TrafficJamChaufferFromL3;
    private SwitchToL0ManualDrivingFromL3Rule switchToL0ManualDrivingFromL3;
    private SwitchToL0ManualDrivingFromL2AdaptiveCruiseControlRule switchToL0ManualDrivingFromL2AdaptiveCruiseControlRule;
    private SwitchToL0ManualDrivingFromL2LaneKeepingAssistRule switchToL0ManualDrivingFromL2LaneKeepingAssistRule;
    private ReplaceFrontDistanceSensorRule replaceFrontDistanceSensorRule;
    private ReplaceLeftDistanceSensorRule replaceLeftDistanceSensorRule;
    private ReplaceRightDistanceSensorRule replaceRightDistanceSensorRule;
    private ReplaceRearDistanceSensorRule replaceRearDistanceSensorRule;
    private SwitchToL3HighwayChaufferFromL3Rule switchToL3HighwayChaufferFromL3Rule;
    private SwitchToL3CityChaufferFromL3Rule switchToL3CityChaufferFromL3Rule;
    private SwitchToL2LaneKeepingAssistFromL2 switchToL2LaneKeepingAssistFromL2Rule;
    private SwitchToL2AdaptiveCruiseControlFromL2Rule switchToL2AdaptiveCruiseControlFromL2Rule;
	private EnableSteeringWheelHapticVibrationRule enableSteeringWheelHapticVibrationRule;
	private DisableSteeringWheelHapticVibrationRule disableSteeringWheelHapticVibrationRule;

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		this.enableNotificationsInL1Rule = new EnableNotificationsInL1Rule(bundleContext);
		String enableNotificationSystemInL1ServiceFilter = createFilter(CurrentDrivingServiceStatus.class, NotificationServiceHealthStatus.class);
		context.addServiceListener(this.enableNotificationsInL1Rule, enableNotificationSystemInL1ServiceFilter);

		this.switchToL0ManualDrivingFromL1 = new SwitchToL0ManualDrivingFromL1Rule(context);
		String swithToL0FromL1Filter = createFilter(CurrentDrivingServiceStatus.class, LineSensorsHealthStatus.class, DistanceSensorHealthStatus.class);
        context.addServiceListener(this.switchToL0ManualDrivingFromL1, swithToL0FromL1Filter);

        this.switchToL0ManualDrivingFromL3 = new SwitchToL0ManualDrivingFromL3Rule(context);
        context.addServiceListener(this.switchToL0ManualDrivingFromL3, swithToL0FromL1Filter);

        this.switchToL0ManualDrivingFromL2AdaptiveCruiseControlRule = new SwitchToL0ManualDrivingFromL2AdaptiveCruiseControlRule(context);
        String swithToL0FromL2ACCFilter = createFilter(CurrentDrivingServiceStatus.class, DistanceSensorHealthStatus.class);
        context.addServiceListener(this.switchToL0ManualDrivingFromL2AdaptiveCruiseControlRule, swithToL0FromL2ACCFilter);

        this.switchToL0ManualDrivingFromL2LaneKeepingAssistRule = new SwitchToL0ManualDrivingFromL2LaneKeepingAssistRule(context);
        String swithToL0FromL2LKAFilter = createFilter(CurrentDrivingServiceStatus.class, LineSensorsHealthStatus.class);
        context.addServiceListener(this.switchToL0ManualDrivingFromL2LaneKeepingAssistRule, swithToL0FromL2LKAFilter);

		this.switchToL1AssistedDrivingFromL0Rule = new SwitchToL1AssistedDrivingFromL0Rule(context);
        String swithToL1FromL0Filter = createFilter(CurrentDrivingServiceStatus.class, LineSensorsHealthStatus.class, DistanceSensorHealthStatus.class);
        context.addServiceListener(this.switchToL1AssistedDrivingFromL0Rule, swithToL1FromL0Filter);

        this.switchToL1AssistedDrivingFromL3Rule = new SwitchToL1AssistedDrivingFromL3Rule(context);
        String swithToL1FromL3Filter = createFilter(CurrentDrivingServiceStatus.class, LineSensorsHealthStatus.class, DistanceSensorHealthStatus.class, RoadContext.class);
        context.addServiceListener(this.switchToL1AssistedDrivingFromL3Rule, swithToL1FromL3Filter);

        this.switchToL2AdaptiveCruiseControlFromL1Rule = new SwitchToL2AdaptiveCruiseControlFromL1Rule(context);
        String swithToL2AccFilter = createFilter(RoadContext.class, CurrentDrivingServiceStatus.class, EngineHealthStatus.class, DistanceSensorHealthStatus.class);
        context.addServiceListener(this.switchToL2AdaptiveCruiseControlFromL1Rule, swithToL2AccFilter);

        this.switchToL2AdaptiveCruiseControlFromL2Rule = new SwitchToL2AdaptiveCruiseControlFromL2Rule(context);
        context.addServiceListener(this.switchToL2AdaptiveCruiseControlFromL2Rule, swithToL2AccFilter);

        this.switchToL2LaneKeepingAssistFromL1Rule = new SwitchToL2LaneKeepingAssistFromL1(context);
        String swithToL2LaneFilter = createFilter(CurrentDrivingServiceStatus.class, LineSensorsHealthStatus.class, RoadContext.class, Steering.class);
        context.addServiceListener(this.switchToL2LaneKeepingAssistFromL1Rule, swithToL2LaneFilter);

        this.switchToL2LaneKeepingAssistFromL2Rule = new SwitchToL2LaneKeepingAssistFromL2(context);
        context.addServiceListener(this.switchToL2LaneKeepingAssistFromL2Rule, swithToL2LaneFilter);

        this.switchToL2LaneKeepingAssistFromL3Rule = new SwitchToL2LaneKeepingAssistFromL3(context);
        context.addServiceListener(this.switchToL2LaneKeepingAssistFromL3Rule, swithToL2LaneFilter);

        this.replaceFrontDistanceSensorRule = new ReplaceFrontDistanceSensorRule(bundleContext);
        String replaceDistanceSensorRuleServiceFilter = createFilter(CurrentDrivingServiceStatus.class, DistanceSensorHealthStatus.class);
        context.addServiceListener(this.replaceFrontDistanceSensorRule, replaceDistanceSensorRuleServiceFilter);

        this.replaceLeftDistanceSensorRule = new ReplaceLeftDistanceSensorRule(bundleContext);
        context.addServiceListener(this.replaceLeftDistanceSensorRule, replaceDistanceSensorRuleServiceFilter);

        this.replaceRightDistanceSensorRule = new ReplaceRightDistanceSensorRule(bundleContext);
        context.addServiceListener(this.replaceRightDistanceSensorRule, replaceDistanceSensorRuleServiceFilter);

        this.replaceRearDistanceSensorRule = new ReplaceRearDistanceSensorRule(bundleContext);
        context.addServiceListener(this.replaceRearDistanceSensorRule, replaceDistanceSensorRuleServiceFilter);

        String swithToL3RuleFilter = createFilter(
                CurrentDrivingServiceStatus.class,
                DistanceSensorHealthStatus.class,
                EngineHealthStatus.class,
                FallbackPlanHealthStatus.class,
                HumanSensorsHealthStatus.class,
                LineSensorsHealthStatus.class,
                NotificationServiceHealthStatus.class,
                RoadContext.class,
                Steering.class);

        this.switchToL3CityChaufferFromL2Rule = new SwitchToL3CityChaufferFromL2Rule(context);
        context.addServiceListener(this.switchToL3CityChaufferFromL2Rule, swithToL3RuleFilter);

        this.switchToL3CityChaufferFromL3Rule = new SwitchToL3CityChaufferFromL3Rule(context);
        context.addServiceListener(this.switchToL3CityChaufferFromL3Rule, swithToL3RuleFilter);

        this.switchToL3HighwayChaufferFromL2Rule = new SwitchToL3HighwayChaufferFromL2Rule(context);
        context.addServiceListener(this.switchToL3HighwayChaufferFromL2Rule, swithToL3RuleFilter);

        this.switchToL3HighwayChaufferFromL3Rule = new SwitchToL3HighwayChaufferFromL3Rule(context);
        context.addServiceListener(this.switchToL3HighwayChaufferFromL3Rule, swithToL3RuleFilter);

        this.swithToL3TrafficJamChaufferFromL2 = new SwitchToL3TrafficJamChaufferFromL2(context);
        context.addServiceListener(this.swithToL3TrafficJamChaufferFromL2, swithToL3RuleFilter);

        this.swithToL3TrafficJamChaufferFromL3 = new SwitchToL3TrafficJamChaufferFromL3(context);
        context.addServiceListener(this.swithToL3TrafficJamChaufferFromL3, swithToL3RuleFilter);

        // Interaction mechanisms.
        String steeringWheelHapticVibrationFilter = createFilter(DriverContext.class, NotificationServiceHealthStatus.class);

        this.enableSteeringWheelHapticVibrationRule = new EnableSteeringWheelHapticVibrationRule(bundleContext);
        context.addServiceListener(this.enableSteeringWheelHapticVibrationRule, steeringWheelHapticVibrationFilter);

        this.disableSteeringWheelHapticVibrationRule = new DisableSteeringWheelHapticVibrationRule(bundleContext);
        context.addServiceListener(this.disableSteeringWheelHapticVibrationRule, steeringWheelHapticVibrationFilter);
	}

	public void stop(BundleContext bundleContext) throws Exception {
	    context.removeServiceListener(this.enableNotificationsInL1Rule);
	    this.enableNotificationsInL1Rule = null;

	    context.removeServiceListener(this.replaceFrontDistanceSensorRule);
        this.replaceFrontDistanceSensorRule = null;

        context.removeServiceListener(this.replaceLeftDistanceSensorRule);
        this.replaceLeftDistanceSensorRule = null;

        context.removeServiceListener(this.replaceRightDistanceSensorRule);
        this.replaceRightDistanceSensorRule = null;

        context.removeServiceListener(this.replaceRearDistanceSensorRule);
        this.replaceRearDistanceSensorRule = null;

	    context.removeServiceListener(this.switchToL0ManualDrivingFromL1);
        this.switchToL0ManualDrivingFromL1 = null;

        context.removeServiceListener(this.switchToL0ManualDrivingFromL3);
        this.switchToL0ManualDrivingFromL3 = null;

        context.removeServiceListener(this.switchToL0ManualDrivingFromL2AdaptiveCruiseControlRule);
        this.switchToL0ManualDrivingFromL2AdaptiveCruiseControlRule = null;

        context.removeServiceListener(this.switchToL0ManualDrivingFromL2LaneKeepingAssistRule);
        this.switchToL0ManualDrivingFromL2LaneKeepingAssistRule = null;

	    context.removeServiceListener(this.switchToL1AssistedDrivingFromL0Rule);
        this.switchToL1AssistedDrivingFromL0Rule = null;

        context.removeServiceListener(this.switchToL1AssistedDrivingFromL3Rule);
        this.switchToL1AssistedDrivingFromL3Rule = null;

	    context.removeServiceListener(this.switchToL2AdaptiveCruiseControlFromL1Rule);
	    this.switchToL2AdaptiveCruiseControlFromL1Rule = null;

	    context.removeServiceListener(this.switchToL2AdaptiveCruiseControlFromL2Rule);
        this.switchToL2AdaptiveCruiseControlFromL2Rule = null;

	    context.removeServiceListener(this.switchToL2LaneKeepingAssistFromL1Rule);
	    this.switchToL2LaneKeepingAssistFromL1Rule = null;

	    context.removeServiceListener(this.switchToL2LaneKeepingAssistFromL2Rule);
        this.switchToL2LaneKeepingAssistFromL2Rule = null;

	    context.removeServiceListener(this.switchToL2LaneKeepingAssistFromL3Rule);
        this.switchToL2LaneKeepingAssistFromL3Rule = null;

	    context.removeServiceListener(this.switchToL3CityChaufferFromL2Rule);
        this.switchToL3CityChaufferFromL2Rule = null;

        context.removeServiceListener(this.switchToL3CityChaufferFromL3Rule);
        this.switchToL3CityChaufferFromL3Rule = null;

        context.removeServiceListener(this.switchToL3HighwayChaufferFromL2Rule);
        this.switchToL3HighwayChaufferFromL2Rule = null;

        context.removeServiceListener(this.switchToL3HighwayChaufferFromL3Rule);
        this.switchToL3HighwayChaufferFromL3Rule = null;

        context.removeServiceListener(this.swithToL3TrafficJamChaufferFromL2);
        this.swithToL3TrafficJamChaufferFromL2 = null;

        context.removeServiceListener(this.swithToL3TrafficJamChaufferFromL3);
        this.swithToL3TrafficJamChaufferFromL3 = null;

        context.removeServiceListener(this.enableSteeringWheelHapticVibrationRule);
        this.enableSteeringWheelHapticVibrationRule = null;

        context.removeServiceListener(this.disableSteeringWheelHapticVibrationRule);
        this.disableSteeringWheelHapticVibrationRule = null;

		Activator.context = null;
	}

	// TODO: Move to an utilities class.
	// Obtained from: https://gist.github.com/toelen/1370316
	private static String createFilter(Class<?>... interfaces) {
        StringBuilder builder = new StringBuilder();

        builder.append("( |");
        for (Class<?> clazz : interfaces) {
            builder.append("(objectclass=" + clazz.getName()
                    + ") ");
        }

        builder.append(" ) ");
        return builder.toString();
	}

}
