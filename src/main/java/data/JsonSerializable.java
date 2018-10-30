package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface JsonSerializable<T> {

    Gson gson = new GsonBuilder().create();

    default String toJsonString(T obj) {
        return gson.toJson(obj);
    }
}
