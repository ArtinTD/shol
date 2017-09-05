package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import org.jsoup.nodes.Document;


import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

public class CrawlerQueue implements Queue {

    private Consumer consumer;
    private Producer producer = new Producer();
    private final String TOPIC = "artinChii";

    public CrawlerQueue() {
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
        consumer = new Consumer("8", TOPIC, properties);
        Thread crawlerKafkaConsumingThread = new Thread(consumer);
        crawlerKafkaConsumingThread.start();
    }

    @Override
    public String get() throws InterruptedException {

        return (String) consumer.get();
    }

    public void send(Object element) {
        if (element instanceof String) {
            String toSend = (String) element;
            producer.send(toSend, TOPIC);
        } else if (element instanceof HashMap) {

            ((HashMap) element).forEach((k, v) -> producer.send(k.toString(), TOPIC));

        } else if (element instanceof ArrayList) {

            for (String s : (ArrayList<String>) element) {
                producer.send(s, TOPIC);
            }
        }
    }


}
