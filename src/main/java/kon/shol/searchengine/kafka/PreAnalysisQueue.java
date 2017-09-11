package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import kon.shol.searchengine.crawler.Storage;
import org.jsoup.nodes.Document;


import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

public class PreAnalysisQueue implements Queue {

    private Consumer consumer;
    private Producer producer = new Producer();
    private String TOPIC = "CrawlerQueue";
    private Storage storage;

    public PreAnalysisQueue(String topic, Storage storage) {
        TOPIC = topic;
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
        consumer = new Consumer("8", TOPIC, properties, storage);
        Thread crawlerKafkaConsumingThread = new Thread(consumer);
        crawlerKafkaConsumingThread.start();
        this.storage = storage;
    }

    @Override
    public String get() throws InterruptedException {

        return (String) consumer.get();
    }

    @Override
    public void send(String message) {

        producer.send(message, TOPIC);
    }

    @Override
    public void send(Object[] messages) throws IOException {

        for (String message: storage.removeExisting(messages)){
            producer.send(message, TOPIC);
        }
    }
}
