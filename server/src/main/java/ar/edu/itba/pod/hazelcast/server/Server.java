package ar.edu.itba.pod.hazelcast.server;

import ar.edu.itba.pod.hazelcast.api.ConfigHandler;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

public class Server {

    private static final String MANAGEMENT_CENTER_URL =  "http://localhost:8080/mancenter-3.8.5/";
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        if(args.length == 0) {
            logger.error("Attempted to initialize Hazelcast server without selecting a network interface");
            throw new IllegalArgumentException("No network interface provided");
        }

        String networkInterface = args[0];

        logger.info("Creating node");

        // Config
        Config config = new Config();

        // Parse config file
        ConfigHandler cf = ConfigHandler.parseConfigFile();

        // Group config
        GroupConfig groupConfig = new GroupConfig().setName(cf.getGroupName()).setPassword(cf.getGroupPassword());
        config.setGroupConfig(groupConfig);

        // Network config
        MulticastConfig multicastConfig = new MulticastConfig();

        JoinConfig joinConfig = new JoinConfig()
                .setMulticastConfig(multicastConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(Collections.singletonList(networkInterface))
                .setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

        // Management Center Config
        // Add the following flags to the vm before running a new node for the cluster:
        // --add-opens java.management/sun.management=ALL-UNNAMED
        // --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
        if(cf.runManagementCenter()) {
            ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
                    .setUrl(MANAGEMENT_CENTER_URL)
                    .setEnabled(true);
            config.setManagementCenterConfig(managementCenterConfig);
            logger.info("Adding Management Center configuration. URL: " + MANAGEMENT_CENTER_URL);
        }

        // Create new node
        Hazelcast.newHazelcastInstance(config);
        logger.info("Node created successfully");
    }
}