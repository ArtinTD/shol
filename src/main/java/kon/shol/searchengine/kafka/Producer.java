package kon.shol.searchengine.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class Producer {

    private KafkaProducer<String, String> producer;
    private static final String PRODUCER_TYPE = "producer.type";
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.kafka.Producer.class);

    public Producer() {

        Properties properties = new Properties();
        properties.put(PRODUCER_TYPE,
                "async");
        properties.put(ACKS_CONFIG,
                "all");
        properties.put(BUFFER_MEMORY_CONFIG,
                "1000000");
        properties.put(BATCH_SIZE_CONFIG,
                "16384");
        properties.put(RETRIES_CONFIG,
                "1000");
        properties.put(LINGER_MS_CONFIG,
                "50");
        properties.put(BOOTSTRAP_SERVERS_CONFIG,
                "188.165.230.122:9092,188.165.235.136:9092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(CLIENT_ID_CONFIG,
                String.valueOf(Thread.currentThread().getId()));
        producer = new KafkaProducer<>(properties);
    }

    public void batchSend(ArrayList<String> messages, String topic) {

        for (String message : messages) {
            send(message, topic);
        }
    }

    public void send(String message, String topic) {

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(message, topic);
        producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                //todo handle future
                logger.error(exception.getMessage());
            }
            if (exception instanceof UnknownServerException) {
                logger.error("Kafka UnknownServerException, application closed!");
                System.exit(0);
            }
        });
    }
}
