package kon.shol.searchengine.serializer;

import com.google.gson.Gson;

public class Serializer {
    public String serialize(Object payload) {
        return new Gson().toJson(payload);
    }
}
