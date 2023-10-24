package ar.edu.itba.pod.hazelcast.client;

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
            final Strategy strategy = strategyMapper.getStrategy(arguments.getStrategy());

            // Client Network Config
            final ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
            clientNetworkConfig.addAddress(arguments.getAddresses());
            clientConfig.setNetworkConfig(clientNetworkConfig);

            // Hazelcast Instance
            final HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);

            String startLoadingTimestamp = LocalDateTime.now().format(formatter) + " - Inicio de la lectura del archivo";
           strategy.loadData(arguments, hz);
            String finishLoadingTimestamp = LocalDateTime.now().format(formatter) + " - Fin de la lectura del archivo";

            String startRunningQueryTimestamp = LocalDateTime.now().format(formatter) + " - Inicio del trabajo map/reduce";
            strategy.runClient(arguments, hz);
            String stopRunningQueryTimestamp = LocalDateTime.now().format(formatter) + " - Fin del trabajo map/reduce";

            String[] timestamps = new String[]{startLoadingTimestamp, finishLoadingTimestamp, startRunningQueryTimestamp, stopRunningQueryTimestamp};

            File logFile = new File(arguments.getOutPath() + outputFileName);

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
            for (String timestamp : timestamps) {
                writer.write(timestamp);
                writer.newLine();
            }
            writer.close();
            logger.info(outputFileName + " created successfully");
        } catch (ClientException e) {
            System.out.println("Client error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error writing " + outputFileName + ": " + e.getMessage() );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            HazelcastClient.shutdownAll();
        }
    }
}