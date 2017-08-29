package kon.shol.searchengine.serializer;


import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        Parser parser = new Parser();
        Map<String, String> map = new HashMap<>();
        map.put("pro1", "v2");
        map.put("pro2", "v3");
        Document document = Jsoup.connect("http://wikipedia.org").get();
//        String json = serializer.serialize(parser.parse(document));
//        System.out.println(deserializer.deserialize(json, PageData.class).getLinks());
//        String json2 = serializer.serialize(map);
//        System.out.println(deserializer.deserialize(json2, HashMap.class).get("pro1"));
    }
}
