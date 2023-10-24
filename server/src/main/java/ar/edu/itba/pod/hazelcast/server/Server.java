package ar.edu.itba.pod.hazelcast.server;

import ar.edu.itba.pod.hazelcast.api.CredentialsParser;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        if(args.length == 0) {
            logger.error("Attempted to initialize Hazelcast server without selecting a network interface");
            throw new IllegalArgumentException("No network interface provided");
        }

        String networkInterface = args[0];
        System.out.printf(networkInterface);

        logger.info("Setting up server");

        // Config
        Config config = new Config();

        // Get credentials from credentials.json
        CredentialsParser cp = CredentialsParser.parseCredentialsFile();

        // Group config
        GroupConfig groupConfig = new GroupConfig().setName(cp.getGroupName()).setPassword(cp.getGroupPassword());
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
        // Add the following flags to the vm before running the server:
        // --add-opens java.management/sun.management=ALL-UNNAMED
        // --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
        // ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
        //        .setUrl("http://localhost:8080/mancenter-3.8.5/")
        //        .setEnabled(true);
        // config.setManagementCenterConfig(managementCenterConfig);

        // Start cluster
        Hazelcast.newHazelcastInstance(config);
    }
}