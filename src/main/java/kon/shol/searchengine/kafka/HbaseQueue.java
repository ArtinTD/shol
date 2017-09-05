package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;
import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.parser.Parser;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.DEFAULT_FETCH_MAX_BYTES;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

public class HbaseQueue implements Queue{

    private Producer producer;
    private Consumer consumer;
    private Parser parser;
    private final String TOPIC = "HbaseQueue";

    public HbaseQueue() {
        producer = new Producer();
        Properties properties = new Properties();
        properties.put(FETCH_MAX_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
        consumer = new Consumer("1", TOPIC, properties);
        Thread hBaseKafkaConsumingThread = new Thread(consumer);
        hBaseKafkaConsumingThread.start();
        parser = new Parser();
    }

    @Override
    public Document get() throws InterruptedException {
        if (consumer.get() instanceof Document)
            return (Document) consumer.get();
        return null;
    }

    @Override
    public void send(Object element) {

        PageData pageData = (PageData) element;
        String serializedPageData = parser.serialize(pageData);
        producer.send(serializedPageData, TOPIC);
    }
}
