package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.serializer.Serializer;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.DEFAULT_FETCH_MAX_BYTES;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

public class HbaseQueue {

    private Producer producer;
    private Consumer consumer;
    private Serializer serializer;

    private final String TOPIC = "HbaseQueue";

    public HbaseQueue() {
        producer = new Producer();
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG,
                DEFAULT_FETCH_MAX_BYTES);
        consumer = new Consumer("1", TOPIC, properties);
        serializer = new Serializer();
    }

    public void send(PageData pageData) {

        String serializedPageData = serializer.serialize(pageData);
        producer.send(serializedPageData, TOPIC);
    }

}
