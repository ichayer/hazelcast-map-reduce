package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.CredentialsParser;
import ar.edu.itba.pod.hazelcast.client.exceptions.ClientException;
import ar.edu.itba.pod.hazelcast.client.interfaces.Strategy;
import ar.edu.itba.pod.hazelcast.client.interfaces.StrategyMapper;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public abstract class GenericQuery {

    private static final Logger logger = LoggerFactory.getLogger(GenericQuery.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSSS");

    public void run(String[] args, String outputFileName, StrategyMapper strategyMapper) {

        try {
            // Parse arguments
            final Arguments arguments = Parser.parse(args);
            logger.info("Arguments parsed successfully");

            // Hazelcast instance setup
            final HazelcastInstance hz = setup(arguments.getAddresses());
            logger.info("Hazelcast instance created");

            // Get strategy for loading the data and running the query
            final Strategy strategy = strategyMapper.getStrategy(arguments.getStrategy());
            logger.info("Strategy selected: " + arguments.getStrategy());

            String startLoadingTimestamp = LocalDateTime.now().format(formatter) + " - Inicio de la lectura del archivo";
            strategy.loadData(arguments, hz);
            String finishLoadingTimestamp = LocalDateTime.now().format(formatter) + " - Fin de la lectura del archivo";
            logger.info("Data loaded successfully");

            String startRunningQueryTimestamp = LocalDateTime.now().format(formatter) + " - Inicio del trabajo map/reduce";
            strategy.runClient(arguments, hz);
            String stopRunningQueryTimestamp = LocalDateTime.now().format(formatter) + " - Fin del trabajo map/reduce";
            logger.info("Query executed successfully");

            String[] timestamps = new String[]{startLoadingTimestamp, finishLoadingTimestamp, startRunningQueryTimestamp, stopRunningQueryTimestamp};
            String pathname = arguments.getOutPath() + outputFileName;
            createLogFile(pathname, timestamps);
            logger.info("Log file created successfully: " + outputFileName);
            logger.info("Shutting down Hazelcast instance");
        } catch (ClientException e) {
            logger.error("Client error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error writing " + outputFileName + ": " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    private HazelcastInstance setup(String[] addresses) {

        // Client Config
        final ClientConfig clientConfig = new ClientConfig();

        // Get credentials from credentials.json
        final CredentialsParser cp = CredentialsParser.parseCredentialsFile();

        // Group Config
        final GroupConfig groupConfig = new GroupConfig()
                .setName(cp.getGroupName()).setPassword(cp.getGroupPassword());
        clientConfig.setGroupConfig(groupConfig);

        // Client Network Config
        final ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    private void createLogFile(String pathname, String[] timestamps) throws IOException {
        final File logFile = new File(pathname);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
        for (String timestamp : timestamps) {
            writer.write(timestamp);
            writer.newLine();
        }
        writer.close();
    }
}