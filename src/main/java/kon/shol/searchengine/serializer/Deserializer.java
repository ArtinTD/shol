package kon.shol.searchengine.serializer;

import com.google.gson.Gson;

public class Deserializer {
    public <T> T deserialize(String payload, Class<T> tClass){
        return new Gson().fromJson(payload, tClass);
    }
}
