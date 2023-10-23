package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.client.exceptions.ClientException;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;


public abstract class GenericClient {

    private static Logger logger = LoggerFactory.getLogger(GenericClient.class);

    public void run(String[] args) {

        logger.info("Setting up client");
        try {

            // Client Config
            final ClientConfig clientConfig = new ClientConfig();

            // Group Config
            final GroupConfig groupConfig = new GroupConfig()
                    .setName("g4").setPassword("g4-pass");
            clientConfig.setGroupConfig(groupConfig);

            // Parse arguments
            final Arguments arguments = Parser.parse(args);

            // Client Network Config
            final ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
            clientNetworkConfig.addAddress(arguments.getAddresses());
            clientConfig.setNetworkConfig(clientNetworkConfig);

            // Hazelcast Instance
            final HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);

            String startLoadingTimestamp = LocalDateTime.now().toString();
            loadData(arguments, hz);
            String finishLoadingTimestamp = LocalDateTime.now().toString();

            String startRunningQueryTimestamp = LocalDateTime.now().toString();
            runClient(arguments,hz);
            String stopRunningQueryTimestamp = LocalDateTime.now().toString();

        } catch (ClientException e) {
            System.out.println("Client error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    public abstract void loadData(Arguments args, HazelcastInstance hz);

    public abstract void runClient(Arguments args,HazelcastInstance hz);
}