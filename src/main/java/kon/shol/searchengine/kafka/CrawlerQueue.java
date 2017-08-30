package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;
import java.util.Properties;

public class CrawlerQueue implements Queue {

    private Producer producer;
    private Consumer consumer;

    private final String TOPIC = "CrawlerQueue";

    public CrawlerQueue() {
        producer = new Producer();
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
        consumer = new Consumer("0", TOPIC, properties);
        Thread crawlerKafkaConsumingThread = new Thread(consumer);
        crawlerKafkaConsumingThread.start();
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void send(Object element) {

    }
}