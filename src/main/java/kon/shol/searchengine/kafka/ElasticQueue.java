package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import org.jsoup.nodes.Document;

import javax.print.Doc;

import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ElasticQueue implements Queue {

    private Producer producer;
    private Consumer consumer;

    private final String TOPIC = "ElasticQueue";

    public ElasticQueue() {
        producer = new Producer();
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
        consumer = new Consumer("0", TOPIC, properties);
        Thread elasticKafkaConsumingThread = new Thread(consumer);
        elasticKafkaConsumingThread.start();
    }

    @Override
    public Document get() throws InterruptedException {
        if (consumer.get() instanceof Document)
            return (Document) consumer.get();
        return null;
    }

    @Override
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
