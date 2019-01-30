package data;


import com.google.gson.*;
import core.Main;
import core.ServiceLocator;

import java.io.*;
import java.net.URL;
import java.util.*;

// This class will be held in the Service Locator so it will be singleton
public final class DataLoader {

    private Map<String, SavedConnection> savedConnections;
    private JsonObject messages;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    /*

        SAVED CONNECTIONS

    */

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

    /*

        MESSAGES

    */

    public String getMessage(String key) {
        if(this.messages == null)
            this.messages = loadMessages();
        if(messages.has(key))
            return messages.get(key).getAsString();
        else
            return "[ERROR] MISSING KEY:"+key;
    }

    private JsonObject loadMessages() {
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("messages.json");
        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(isr);
        String json = br.lines().collect(StringBuilder::new,StringBuilder::append,StringBuilder::append).toString();
        return gson.fromJson(json, JsonObject.class);
    }
}
