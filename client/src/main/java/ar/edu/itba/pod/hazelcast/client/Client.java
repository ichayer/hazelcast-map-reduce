package ar.edu.itba.pod.hazelcast.client;

import ar.edu.itba.pod.hazelcast.api.models.Station;
import ar.edu.itba.pod.hazelcast.client.utils.Arguments;
import ar.edu.itba.pod.hazelcast.client.utils.CsvFileIterator;
import ar.edu.itba.pod.hazelcast.client.utils.Parser;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        logger.info("Setting up client");

        // Client Config
        ClientConfig clientConfig = new ClientConfig();

        // Group Config
        GroupConfig groupConfig = new GroupConfig()
                .setName("g4").setPassword("g4-pass");
        clientConfig.setGroupConfig(groupConfig);

        //Parse arguments
        Arguments arguments = Parser.parse(args);
        //System.out.println(arguments);

        // Client Network Config
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        //String[] addresses = {"172.20.10.5:5701"};
        clientNetworkConfig.addAddress(arguments.getAddresses());
        clientConfig.setNetworkConfig(clientNetworkConfig);

        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        String mapName = "testMap";

        //IMap<Integer, Station> testMapFromMember = hazelcastInstance.getMap(mapName);

        CsvFileIterator.ParseStationsCsv(arguments.getInPath(),hazelcastInstance.getMap(mapName));

        //testMapFromMember.set(1, "test1");

        //IMap<Integer, String> testMap = hazelcastInstance.getMap(mapName);
        System.out.println(hazelcastInstance.getMap(mapName).get(327));

        // Shutdown
        HazelcastClient.shutdownAll();
    }
}