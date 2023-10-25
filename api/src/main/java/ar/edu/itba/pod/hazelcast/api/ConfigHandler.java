package ar.edu.itba.pod.hazelcast.api;

import com.hazelcast.com.eclipsesource.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigHandler {

    private final String groupName;
    private final String groupPassword;
    private final boolean runManagementCenter;

    public static ConfigHandler parseConfigFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.json"))) {
            JsonObject json = JsonObject.readFrom(reader);
            String groupName = json.getString("groupName", "");
            String groupPassword = json.getString("groupPassword", "");
            boolean runManagementCenter = json.getBoolean("runManagementCenter", false);

            if (groupPassword.isBlank() || groupName.isBlank()) {
                throw new IllegalArgumentException("Invalid config.json");
            }
            return new ConfigHandler(groupName, groupPassword, runManagementCenter);
        } catch (IOException e) {
            throw new RuntimeException("Error reading config.json: " + e.getMessage());
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupPassword() {
        return groupPassword;
    }

    public boolean runManagementCenter() {
        return runManagementCenter;
    }

    private ConfigHandler() {
        throw new AssertionError();
    }

    private ConfigHandler(String groupName, String groupPassword, boolean runManagementCenter) {
        this.groupName = groupName;
        this.groupPassword = groupPassword;
        this.runManagementCenter = runManagementCenter;
    }
}
