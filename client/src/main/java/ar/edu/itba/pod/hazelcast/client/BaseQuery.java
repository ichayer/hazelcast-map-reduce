package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.ConfigHandler;
import ar.edu.itba.pod.hazelcast.client.exceptions.ClientException;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Constants;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;
import ar.edu.itba.pod.hazelcast.client.utils.StrategyMapperImpl;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class BaseQuery {

    private final String[] arguments;
    private final String outputFileName;
    private final StrategyMapper strategyMapper;
    private Logger logger;

    BaseQuery(String[] arguments, String outputFileName, Map<String, Supplier<Strategy>> strategies) {
        this.arguments = Objects.requireNonNull(arguments);
        this.outputFileName = Objects.requireNonNull(outputFileName);
        this.strategyMapper = new StrategyMapperImpl(strategies);
    }

    public void run() {

        try {
            // Parse arguments
            final Arguments arguments = Parser.parse(this.arguments);
            String pathname = arguments.getOutPath() + this.outputFileName;
            setUpLogger(pathname);
            logger.info("Arguments parsed successfully");

            // Hazelcast instance setup
            final HazelcastInstance hz = setup(arguments.getAddresses());
            logger.info("Hazelcast instance created");

            // Get strategy for loading the data and running the query
            final Strategy strategy = this.strategyMapper.getStrategy(arguments.getStrategy());
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
        this.logger = LoggerFactory.getLogger(BaseQuery.class);
    }
}