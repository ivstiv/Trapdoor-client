package data;

public class SavedConnection implements JsonSerializable<SavedConnection>{
    private final String ip, username, password;
    private int port;

    public SavedConnection(String ip, int port, String username, String password) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.username = username;
    }

    public static SavedConnection buildFromJson(String json) {
        return gson.fromJson(json, SavedConnection.class);
    }

    @Override
    public String toString() {
        return toJsonString(this);
    }
}
