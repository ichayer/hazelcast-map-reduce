package ar.edu.itba.pod.hazelcast.api;

import com.hazelcast.com.eclipsesource.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CredentialsParser {

    private final String groupName;
    private final String groupPassword;

    public static CredentialsParser parseCredentialsFile() {
        try(BufferedReader reader = new BufferedReader(new FileReader("credentials.json"))) {
            JsonObject json =  JsonObject.readFrom(reader);
            String groupName = json.getString("groupName", "");
            String groupPassword = json.getString("groupPassword", "");
            if (groupPassword.isBlank() || groupName.isBlank()) {
                throw new IllegalArgumentException("Invalid credentials.json");
            }
            return new CredentialsParser(groupName, groupPassword);
        } catch (IOException e) {
            throw new RuntimeException("Error reading credentials.json: " + e.getMessage());
        }
    }
    public String getGroupName() {
        return groupName;
    }

    public String getGroupPassword() {
        return groupPassword;
    }

    private CredentialsParser() {
        throw new AssertionError();
    }

    private CredentialsParser(String groupName, String groupPassword) {
        this.groupName = groupName;
        this.groupPassword = groupPassword;
    }
}
