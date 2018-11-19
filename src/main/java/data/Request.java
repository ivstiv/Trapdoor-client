package data;


import com.google.gson.*;
import exceptions.MalformedRequestException;


/*
    Immutable data class with deep copies!
    Don't try to change the content once set.
 */
public class Request {

    private final RequestType type;
    private final        long timestamp;
    private final        String timezone; // set as string eg. GMT+2
    private final        JsonObject content;
    private final static Gson gson = new GsonBuilder().create();

    /* Json string to Request object */
    public Request(String json) throws MalformedRequestException {
        if(isJSONValid(json)) {
            JsonObject req = gson.fromJson(json, JsonObject.class);
            try{
                this.type = gson.fromJson(req.get("type").getAsString(), RequestType.class);
                this.timestamp = req.get("timestamp").getAsLong();
                this.timezone = gson.fromJson(req.get("timezone").getAsString(), String.class);
                this.content = gson.fromJson(req.get("content"), JsonObject.class);

            }catch(NullPointerException e){
                throw new MalformedRequestException("Malformed request, invalid values!\n"+json);
            }
            if(!this.isValid()) {
                throw new MalformedRequestException("Malformed request, invalid values!");
            }

        }else{
            throw new MalformedRequestException("Malformed request, invalid json syntax!");
        }
    }

    public Request(RequestType type, JsonObject content) {
        this(type, content, System.currentTimeMillis(), "GMT+2");
    }

    public Request(RequestType type, JsonObject content, long timestamp, String timezone) {
        this.type = type;
        this.content = content.deepCopy();
        this.timestamp = timestamp;
        this.timezone = timezone;

        if(!this.isValid()) {
            try {
                throw new MalformedRequestException("Malformed request, invalid values!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 18-Nov-18 why is timestamp missing in the following check ? should be tested
    public boolean isValid() {
        if(type == null || timezone == null || content == null)
            return false;
        return true;
    }

    public static boolean isJSONValid(String test) {

        JsonParser parser = new JsonParser();

        try{
            JsonElement j = parser.parse(test);
            if(j.isJsonArray() || j.isJsonObject()) {
                return true;
            }
            return false;
        }catch(JsonSyntaxException e){
            return false;
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    /* Getters */
    public JsonObject getContent() {
        return content.deepCopy();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String  getTimezone() {
        return timezone;
    }

    public RequestType getType() {
        return type;
    }
}