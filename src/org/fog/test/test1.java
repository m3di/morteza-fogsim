package org.fog.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.FogBroker;
import org.fog.entities.PhysicalTopology;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
//import org.fog.placement.ModulePlacementOnlyCloud;
import org.fog.utils.JsonToTopology;

/**
 * Simulation setup for EEG Beam Tractor Game extracting physical topology
 *
 * @author Harshit Gupta
 *
 */
public class test1 {

    public static void main(String[] args) {

        Log.printLine("Starting VRGame...");

        try {
            Log.disable();
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = true; // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            String appId = "vr_game";

            FogBroker broker = new FogBroker("broker");

            Application application = createApplication(appId, broker.getId());
            application.setUserId(broker.getId());

            /*
             * Creating the physical topology from specified JSON file
             */
            PhysicalTopology physicalTopology = JsonToTopology.getPhysicalTopology(broker.getId(), appId, "topologies/morteza-1");

            Controller controller = new Controller("master-controller", physicalTopology.getFogDevices(), physicalTopology.getSensors(),
                    physicalTopology.getActuators());

            controller.submitApplication(
                    application,
                    0,
                    new ModulePlacementEdgewards(
                            physicalTopology.getFogDevices(),
                            physicalTopology.getSensors(),
                            physicalTopology.getActuators(),
                            application,
                            ModuleMapping.createModuleMapping()));
//                                new ModulePlacementOnlyCloud(
//                                        physicalTopology.getFogDevices(),
//                                        physicalTopology.getSensors(),
//                                        physicalTopology.getActuators(),
//                                        application
//                                )
//            );

            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            Log.printLine("VRGame finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    @SuppressWarnings({"serial"})
    private static Application createApplication(String appId, int userId) {

        Application application = Application.createApplication(appId, userId);

        application.addAppModule("module_1", 500, 1024, 250);
        application.addAppModule("module_2", 1000, 4096, 500);
        application.addAppModule("module_3", 2000, 2048, 1000);
        application.addAppModule("module_4", 1500, 1024, 300);
        application.addAppModule("module_5", 3000, 6144, 2000);
        application.addAppModule("module_6", 1500, 8192, 500);

//        application.addAppModule("module_1", 10);
//        application.addAppModule("module_2", 10);
//        application.addAppModule("module_3", 10);
//        application.addAppModule("module_4", 10);
//        application.addAppModule("module_5", 10);
//        application.addAppModule("module_6", 10);

        application.addTupleMapping("module_1", "TEMP", "TT_2", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_1", "TT_9", "ACTUATOR_B", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_1", "TT_11", "ACTUATOR_A", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_2", "TT_2", "TT_3", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_2", "TT_8", "TT_9", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_3", "TT_3", "TT_4", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_3", "TT_7", "TT_8", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_4", "TT_4", "TT_5", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_4", "TT_6", "TT_7", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_5", "TT_5", "TT_6", new FractionalSelectivity(1.0));
        application.addTupleMapping("module_6", "TT_10", "TT_11", new FractionalSelectivity(1.0));

        application.addAppEdge("TEMP", "module_1", 3000, 500, "TEMP", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("module_1", "module_2", 6000, 500, "TT_2", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("module_2", "module_3", 6000, 500, "TT_3", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("module_3", "module_4", 6000, 500, "TT_4", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("module_4", "module_5", 6000, 500, "TT_5", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("module_5", "module_4", 1000, 500, "TT_6", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_4", "module_3", 1000, 500, "TT_7", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_3", "module_2", 1000, 500, "TT_8", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_2", "module_1", 1000, 500, "TT_9", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_5", "module_6", 100, 1500, 1000, "TT_10", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_6", "module_1", 100, 1500, 1000, "TT_11", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("module_1", "MOTOR", 2000, 500, "ACTUATOR_A", Tuple.DOWN, AppEdge.ACTUATOR);
        application.addAppEdge("module_1", "MOTOR", 2000, 500, "ACTUATOR_B", Tuple.DOWN, AppEdge.ACTUATOR);

        final AppLoop loop1 = new AppLoop(new ArrayList<String>() {
            {
                add("TEMP");
                add("module_1");
                add("module_2");
                add("module_3");
                add("module_4");
                add("module_5");
                add("module_4");
                add("module_3");
                add("module_2");
                add("module_1");
                add("MOTOR");
            }
        });
        final AppLoop loop2 = new AppLoop(new ArrayList<String>() {
            {
                add("module_5");
                add("module_6");
                add("module_1");
                add("MOTOR");
            }
        });
        List<AppLoop> loops = new ArrayList<AppLoop>() {
            {
                add(loop1);
                add(loop2);
            }
        };

        application.setLoops(loops);

        //GeoCoverage geoCoverage = new GeoCoverage(-100, 100, -100, 100);
        return application;
    }
}
