package data;


import com.google.gson.*;

import java.io.*;
import java.util.*;

// This class will be held in the Service Locator so it will be singleton
public final class DataLoader {

    private Map<String, SavedConnection> savedConnections;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Map<String, SavedConnection> getSavedConnections() {
        if(savedConnections == null)
            savedConnections = loadSavedConnections();
        return Collections.unmodifiableMap(savedConnections);
    }

    public void addSavedConnection(SavedConnection sc) {
        if(savedConnections == null)
            savedConnections = loadSavedConnections();
        savedConnections.put(sc.getIp(), sc);
        updateSavedConnections();
    }

    public void removeSavedConnection(SavedConnection sc) {
        if(savedConnections == null)
            savedConnections = loadSavedConnections();
        savedConnections.remove(sc.getIp());
        updateSavedConnections();
    }

    private void updateSavedConnections() {
        File connectionsFile = new File(System.getProperty("user.home")+File.separator+"connections.json");
        if(!connectionsFile.exists()) {
            try {
                connectionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(connectionsFile);
            JsonArray arr = new JsonArray();
            for(SavedConnection e : savedConnections.values()) {
                JsonObject jo = new JsonObject();
                jo.addProperty("ip", e.getIp());
                jo.addProperty("username", e.getUsername());
                jo.addProperty("password", e.getPassword());
                jo.addProperty("port", e.getPort());
                arr.add(jo);
            }
            fw.write(gson.toJson(arr));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<String, SavedConnection> loadSavedConnections() {
        File connectionsFile = new File(System.getProperty("user.home")+File.separator+"connections.json");
        if(connectionsFile.exists()) {
            try {
                FileReader fr = new FileReader(connectionsFile);
                JsonArray arr = gson.fromJson(fr, JsonArray.class);
                if(arr != null) {
                    Map<String, SavedConnection> cons = new HashMap<>();
                    for(JsonElement e : arr) {
                        SavedConnection c = SavedConnection.buildFromJson(e.getAsJsonObject().toString());
                        cons.put(c.getIp(), c);
                    }
                    return cons;
                }else{
                    return new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            return new HashMap<>();
        }
        return new HashMap<>();
    }
}
