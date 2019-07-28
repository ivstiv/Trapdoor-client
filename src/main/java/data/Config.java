package data;

import com.google.gson.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.function.Predicate;

public class Config {

    private static JsonObject properties = new JsonObject();
    private static File config = new File(System.getProperty("user.home")+File.separator+"config.json");
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Config() {}

    public static JsonElement getProperty(String key) {
        init();
        return properties.get(key);
    }

    public static void setProperty(String key, JsonPrimitive value) {
        if(properties.keySet().contains(key)) {

            properties.add(key, value);
        }else{
            System.out.println("[ERROR] Trying to add invalid property to config:"+key);
        }
    }

    public static int getInt(String key) {
        init();
        return properties.get(key).getAsInt();
    }

    public static String getString(String key) {
        init();
        return properties.get(key).getAsString();
    }

    public static JsonArray getJsonArray(String key) {
        init();
        return properties.getAsJsonArray(key);
    }

    public static void updateFile() {
        if(!isExported())
            exportDefault();

        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(config));
            FileWriter fw = new FileWriter(config);

            // TODO: 03-Feb-19 this will delete all comments!
            fw.write(gson.toJson(properties));
            fw.close();

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Could not find the config!");
        } catch (IOException e) {
            System.out.println("IOException while updating the config:"+e.getMessage());
        }
    }

    public static void refresh() {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(config));
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Could not update config!");
            e.printStackTrace();
            return;
        }
        BufferedReader br = new BufferedReader(isr);
        // filter out the comments
        Predicate<String> filter = line -> !line.trim().startsWith("#");
        String cleanJson = br.lines()
                .filter(filter)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        properties = gson.fromJson(cleanJson, JsonObject.class);
    }

    private static void init() {
        if(!isExported())
            exportDefault();

        if(properties.size() == 0) {
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(new FileInputStream(config));
            } catch (FileNotFoundException e) {
                System.out.println("[ERROR] Could not initialise config!");
                e.printStackTrace();
            }

            BufferedReader br = new BufferedReader(isr);

            // filter out the comments
            Predicate<String> filter = line -> !line.trim().startsWith("#");
            String cleanJson = br.lines()
                    .filter(filter)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            properties = gson.fromJson(cleanJson, JsonObject.class);

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void exportDefault() {
        try {
            Files.copy(Config.class.getResourceAsStream("/config.json"), config.toPath());
        } catch (IOException e) {
            System.out.println("[ERROR] Could not export default config!");
            e.printStackTrace();
        }
    }

    private static boolean isExported() {
        if(config.exists())
            return true;
        return false;
    }

}