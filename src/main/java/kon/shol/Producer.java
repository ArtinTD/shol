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

    public Producer() {

        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092,188.165.230.122:9092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("producer.type", "sync");

        producer = new KafkaProducer<String, String>(properties);
    }

    public void sendLink(String link){

        ProducerRecord<String, String> rec = new ProducerRecord<String, String>("arash", link);
        producer.send(rec);
        producer.flush();
    }


    public void createTopic(String topicName, int partitions){

        String zookeeperConnect = "188.165.230.122:2181";
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;

        ZkClient zkClient = new ZkClient(
                zookeeperConnect,
                sessionTimeoutMs,
                connectionTimeoutMs,
                ZKStringSerializer$.MODULE$);

        boolean isSecureKafkaCluster = false;

        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);
        Properties topicConfig = new Properties();
        AdminUtils.createTopic(zkUtils, topicName, partitions, 1, topicConfig, RackAwareMode.Enforced$.MODULE$);
        zkClient.close();
    }
}
