package sua.autonomouscar.controller.rules.autonomy.L2;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.controller.properties.car.CurrentDrivingServiceStatus;
import sua.autonomouscar.controller.properties.car.DistanceSensorHealthStatus;
import sua.autonomouscar.controller.properties.car.EngineHealthStatus;
import sua.autonomouscar.controller.properties.road.RoadContext;
import sua.autonomouscar.controller.rules.AdaptionRuleBase;
import sua.autonomouscar.controller.utils.AutonomousVehicleContextUtils;
import sua.autonomouscar.controller.utils.DistanceSensorPositon;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL2_AdaptiveCruiseControl;
import sua.autonomouscar.driving.l2.acc.L2_AdaptiveCruiseControl;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.Thing;

/**
 * This rule changes the autonomous driving module to {@link IL2_AdaptiveCruiseControl}.
 */
abstract class SwitchToL2AdaptiveCruiseControlRuleBase extends AdaptionRuleBase {
    // The default lateral security distance is of 2.5m (250 cm).
    private static final int LATERAL_SECURITY_DISTANCE = 250;

    protected BundleContext context;

    protected SwitchToL2AdaptiveCruiseControlRuleBase(BundleContext context) {
        this.context = context;
    }

    @Override
    public void evaluateAndExecute() {
        CurrentDrivingServiceStatus currentDrivingServiceStatus = OSGiUtils.getService(context, CurrentDrivingServiceStatus.class);
        DistanceSensorHealthStatus frontDistanceSensorHealthStatus = OSGiUtils.getService(context, DistanceSensorHealthStatus.class, String.format("(%s=%s)", "position", DistanceSensorPositon.FRONT));
        EngineHealthStatus engineHealthStatus = OSGiUtils.getService(context, EngineHealthStatus.class);
        RoadContext roadContext = OSGiUtils.getService(context, RoadContext.class);

        if (currentDrivingServiceStatus == null
                || frontDistanceSensorHealthStatus == null
                || engineHealthStatus == null
                || roadContext == null
                || !evaluateRuleCondition(currentDrivingServiceStatus, roadContext, frontDistanceSensorHealthStatus, engineHealthStatus))
        {
            return;
        }

        System.out.println("[ Controller ] Executing the " + this.getClass().getSimpleName() + " rule.");

        IDrivingService currentDrivingService = AutonomousVehicleContextUtils.findCurrentDrivingService(context);

        ServiceReference<IL2_AdaptiveCruiseControl> l2DrivingServiceReference = context.getServiceReference(IL2_AdaptiveCruiseControl.class);

        IL2_AdaptiveCruiseControl l2DrivingService;

        if (l2DrivingServiceReference != null)
        {
            l2DrivingService = context.getService(l2DrivingServiceReference);
        }
        else
        {
            l2DrivingService = initializeL2AdaptiveCruiseControl();
        }

        IDistanceSensor distanceSensor = AutonomousVehicleContextUtils.findDistanceSensor(context, DistanceSensorPositon.FRONT);
        IEngine engine = OSGiUtils.getService(context, IEngine.class);

        l2DrivingService.setEngine(((Thing) engine).getId());
        l2DrivingService.setFrontDistanceSensor(((Thing) distanceSensor).getId());
        l2DrivingService.setLateralSecurityDistance(LATERAL_SECURITY_DISTANCE);

        // Unregister the current driving service and replace it with the L2_AdaptiveCruiseControl.
        if (currentDrivingService != null)
        {
            ((Thing)currentDrivingService).unregisterThing();
        }

        l2DrivingService.startDriving();

        // Update the distance sensor properties.
        frontDistanceSensorHealthStatus.setActiveDistanceSensorId(((Thing)distanceSensor).getId());
    }

    protected abstract boolean evaluateRuleCondition(CurrentDrivingServiceStatus currentDrivingServiceStatus, RoadContext roadContext, DistanceSensorHealthStatus frontDistanceSensorHealthStatus, EngineHealthStatus engineHealthStatus);

    private IL2_AdaptiveCruiseControl initializeL2AdaptiveCruiseControl() {
        L2_AdaptiveCruiseControl adaptiveCruiseControl = new L2_AdaptiveCruiseControl(context, "L2_AdaptiveCruiseControl");
        adaptiveCruiseControl.registerThing();

        return adaptiveCruiseControl;
    }
}
