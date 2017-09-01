package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;

import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

import java.util.ArrayList;
import java.util.Properties;

public class CrawlerQueue implements Queue {

    private Producer producer;
    private Consumer consumer;

    private final String TOPIC = "CrawlerQueue";

    public CrawlerQueue() {
        producer = new Producer();
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
        consumer = new Consumer("8", TOPIC, properties);
        Thread crawlerKafkaConsumingThread = new Thread(consumer);
        crawlerKafkaConsumingThread.start();
    }

    @Override
    public String get() throws InterruptedException {

        return consumer.get();
    }

    @Override
    public void send(Object element) {
        if (element instanceof String) {
            String toSend = (String) element;
            producer.send(toSend, TOPIC);
        }else if (element instanceof ArrayList){
            for (String s : (ArrayList<String>) element){
                this.send(s);
            }
        }
    }
}
