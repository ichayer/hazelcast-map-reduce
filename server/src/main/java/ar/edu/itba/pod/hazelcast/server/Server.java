package ar.edu.itba.pod.hazelcast.server;

import ar.edu.itba.pod.hazelcast.api.ConfigHandler;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Server {

    private static final String MANAGEMENT_CENTER_URL = "http://localhost:8080/mancenter-3.8.5/";
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        String[] networkInterfaces = getNetworkInterface(args);

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
                .setInterfaces(Arrays.stream(networkInterfaces).collect(Collectors.toList()))
                .setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

        // Management Center Config
        // Add the following flags to the vm before running a new node for the cluster:
        // --add-opens java.management/sun.management=ALL-UNNAMED
        // --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
        if (cf.runManagementCenter()) {
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

    private static String[] getNetworkInterface(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-Daddresses=")) {
                String[] parts = arg.split("=");
                if(parts.length == 2) {
                    String[] ipAddresses = parts[1].substring(1, parts[1].length() - 1).split(";");
                    logger.info("Network interface provided:" + Arrays.toString(ipAddresses));
                    return ipAddresses;
                }
                break;
            }
        }
        logger.error("Attempted to initialize Hazelcast server without selecting a network interface");
        throw new IllegalArgumentException("No network interface provided: -Daddresses");
    }
}