package kon.shol.searchengine.kafka;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;


public class Consumer implements Runnable{

    private final static Logger logger = Logger.getLogger("custom");
    private ArrayBlockingQueue<Object> consumingQueue;
    private KafkaConsumer<String, String> consumer;
    private static final Object LOCK = new Object();
    private static final String BOOTSTRAP_SERVERS = "188.165.235.136:9092,188.165.230.122:9092";

    public Consumer(String groupId, String topic, Properties props) {

        props.put(BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(AUTO_OFFSET_RESET_CONFIG,
                "earliest");
        props.put(SESSION_TIMEOUT_MS_CONFIG,
                "30000");
        props.put(HEARTBEAT_INTERVAL_MS_CONFIG,
                "5000");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG,
                "1000");
        props.put(MAX_POLL_RECORDS_CONFIG,
                "1000");
        props.put(FETCH_MAX_WAIT_MS_CONFIG,
                "100");
        props.put(ENABLE_AUTO_COMMIT_CONFIG,
                "true");
        props.put(GROUP_ID_CONFIG,
                groupId);
        props.put(CLIENT_ID_CONFIG,
                String.valueOf(Thread.currentThread().getId()));
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        consumingQueue = new ArrayBlockingQueue<>(20*1000);
    }

    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, String> records;
            do {
                records = consumer.poll(1000);
            } while (records.isEmpty());
            for (ConsumerRecord<String, String> record : records) {
                consumingQueue.add(record.value());
            }
            synchronized (LOCK) {
                try {
                    if (consumingQueue.size() > 2000) {
                        LOCK.wait();
                    }
                } catch (InterruptedException interruptedException) {
                    logger.fatal("Interruption while waiting to fill the consuming queue");
                }
            }
        }
    }

    public Object get() throws InterruptedException {
        if (consumingQueue.size() <= 2000) {
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
        return consumingQueue.take();
    }
}
