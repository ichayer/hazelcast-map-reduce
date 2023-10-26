package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.ConfigHandler;
import ar.edu.itba.pod.hazelcast.client.exceptions.ClientException;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericQuery {

    private Logger logger;
    public void run(String[] args, String outputFileName, StrategyMapper strategyMapper) {

        try {
            // Parse arguments
            final Arguments arguments = Parser.parse(args);
            String pathname = arguments.getOutPath() + outputFileName;
            setUpLogger(pathname);
            logger.info("Arguments parsed successfully");

            // Hazelcast instance setup
            final HazelcastInstance hz = setup(arguments.getAddresses());
            logger.info("Hazelcast instance created");

            // Get strategy for loading the data and running the query
            final Strategy strategy = strategyMapper.getStrategy(arguments.getStrategy());
            logger.info("Strategy selected: " + arguments.getStrategy());

            strategy.loadData(arguments, hz);
            logger.info("Data loaded successfully");

            strategy.runClient(arguments, hz);
            logger.info("Query executed successfully");

            logger.info("Shutting down Hazelcast instance");
        } catch (ClientException e) {
            setUpLogger(Constants.DEFAULT_PATHNAME);
            logger.error("Client error: " + e.getMessage(), e);
        } catch (Exception e) {
            setUpLogger(Constants.DEFAULT_PATHNAME);
            logger.error("Unknown error: {}", e.getMessage(), e);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    private HazelcastInstance setup(String[] addresses) {

        // Client Config
        final ClientConfig clientConfig = new ClientConfig();

        // Get credentials from credentials.json
        final ConfigHandler cf = ConfigHandler.parseConfigFile();

        // Group Config
        final GroupConfig groupConfig = new GroupConfig()
                .setName(cf.getGroupName()).setPassword(cf.getGroupPassword());
        clientConfig.setGroupConfig(groupConfig);

        // Client Network Config
        final ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    // This method dynamically configures the output log file.
    private void setUpLogger(String pathname) {
        System.setProperty("pathname", pathname);
        this.logger = LoggerFactory.getLogger(GenericQuery.class);
    }
}