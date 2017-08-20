package kon.shol;

import com.lmax.disruptor.dsl.ProducerType;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class Producer {

    private KafkaProducer<String, String> producer;
    private String topic = "urls";

    Producer(String topic) {

        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "188.165.230.122:9092,188.165.235.136:9092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("producer.type", "sync");
        producer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    public void sendLink(String link) {

        producer.send(new ProducerRecord<>(topic, link), (metadata, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
//            System.out.println("Sent:" + link + ", Partition: " + metadata.partition() + ", Offset: "
//                    + metadata.offset());
        });
        producer.flush();
    }

//    public void createTopic(String topicName, int partitions) {
//
//        String zookeeperConnect = "188.165.230.122:2181";
//        int sessionTimeoutMs = 10 * 1000;
//        int connectionTimeoutMs = 8 * 1000;
//
//        ZkClient zkClient = new ZkClient(
//                zookeeperConnect,
//                sessionTimeoutMs,
//                connectionTimeoutMs,
//                ZKStringSerializer$.MODULE$);
//
//        boolean isSecureKafkaCluster = false;
//
//        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);
//        Properties topicConfig = new Properties();
//        System.out.println("Topic Created");
//        AdminUtils.createTopic(zkUtils, topicName, partitions, 1, topicConfig, RackAwareMode.Enforced$.MODULE$);
//        zkClient.close();
//    }

//    @Override
//    public void run() {
//        System.out.println("Produces 3 messages");
//        for (int i = 0; i < 3; i++) {
//            String msg = "Message " + i;
//            producer.send(new ProducerRecord<String, String>(topic, msg), new Callback() {
//                public void onCompletion(RecordMetadata metadata, Exception e) {
//                    if (e != null) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("Sent:" + msg + ", Partition: " + metadata.partition() + ", Offset: "
//                            + metadata.offset());
//                }
//            });
//
//        }
//    }
}
