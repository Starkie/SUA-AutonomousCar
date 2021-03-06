package sua.autonomouscar.controller.rules.autonomy.L3;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.controller.properties.car.CurrentDrivingServiceStatus;
import sua.autonomouscar.controller.properties.road.RoadContext;
import sua.autonomouscar.controller.rules.AdaptionRuleBase;
import sua.autonomouscar.controller.rules.utils.L3ConfigurationUtils;
import sua.autonomouscar.controller.utils.AutonomousVehicleContextUtils;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL2_DrivingService;
import sua.autonomouscar.driving.interfaces.IL3_CityChauffer;
import sua.autonomouscar.driving.l3.citychauffer.L3_CityChauffer;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.Thing;

/**
 * This rule changes the autonomous driving module from {@link IL2_DrivingService} to {@link IL3_CityChauffer}.
 */
abstract class SwitchToL3CityChaufferRuleBase extends AdaptionRuleBase {
    // The default lateral security distance is of 2.5m (250 cm).
    private static final int LATERAL_SECURITY_DISTANCE = 250;

    // The default longitudinal security distance is of 2m (200 cm).
    private static final int LONGITUDINAL_SECURITY_DISTANCE = 200;

    // The reference speed is of 40km/h.
    private static final int REFERENCE_SPEED = 40;

    protected BundleContext context;

    public SwitchToL3CityChaufferRuleBase(BundleContext context) {
        this.context = context;
    }

    @Override
    public void evaluateAndExecute() {
        CurrentDrivingServiceStatus currentDrivingServiceStatus = OSGiUtils.getService(context, CurrentDrivingServiceStatus.class);
        RoadContext roadContext = OSGiUtils.getService(context, RoadContext.class);

        if (currentDrivingServiceStatus == null
                || roadContext == null
                || !evaluateRuleCondition(currentDrivingServiceStatus, roadContext))
        {
            return;
        }

        System.out.println("[ Controller ] Executing the " + this.getClass().getSimpleName() + " rule.");

        IDrivingService currentDrivingService = AutonomousVehicleContextUtils.findCurrentDrivingService(context);

        ServiceReference<IL3_CityChauffer> l3DrivingServiceReference = context.getServiceReference(IL3_CityChauffer.class);

        IL3_CityChauffer l3DrivingService;

        if (l3DrivingServiceReference != null)
        {
            l3DrivingService = context.getService(l3DrivingServiceReference);
        }
        else
        {
            l3DrivingService = initializeL3CityChauffer();
        }

        L3ConfigurationUtils.configureL3DrivingService(
            l3DrivingService,
            this.context,
            REFERENCE_SPEED,
            LATERAL_SECURITY_DISTANCE,
            LONGITUDINAL_SECURITY_DISTANCE);

        // Unregister the current driving service and replace it with the L3_CityChauffer.
        if (currentDrivingService != null)
        {
            currentDrivingService.stopDriving();
            ((Thing)currentDrivingService).unregisterThing();
        }

        l3DrivingService.startDriving();
    }

    protected abstract boolean evaluateRuleCondition(CurrentDrivingServiceStatus currentDrivingServiceStatus, RoadContext roadContext);


    private IL3_CityChauffer initializeL3CityChauffer() {
        L3_CityChauffer cityChauffer = new L3_CityChauffer(context, "L3_CityChauffer");
        cityChauffer.registerThing();

        return cityChauffer;
    }
}
