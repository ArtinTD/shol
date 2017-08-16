package kon.shol;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import java.util.Scanner;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class Producer {

    public void sendLink(String link){

        Properties configProperties = new Properties();
        configProperties.put(BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092");
        configProperties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        ProducerRecord<String, String> rec = new ProducerRecord<String, String>("arash", link);
        producer.send(rec);
        producer.close();
    }


    public void creatTopic(String topicName, int partitions){

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
