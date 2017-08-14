package kon.shol;

import org.apache.kafka.clients.consumer.*;
import java.util.*;
import org.apache.kafka.common.TopicPartition;

public class Consumer {

    public String kafkaGetLink1(String topicName, String groupId){

        Scanner in;
        KafkaConsumer<String, String> kafkaConsumer;
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.230.122:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        kafkaConsumer = new KafkaConsumer<>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        Scanner scanner = new Scanner(System.in);
        while (true){
            String flag = scanner.nextLine();
            if (flag.equals("get")){
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
                    for (ConsumerRecord<String, String> record : records) {
                        return record.value();
                    }
                }
            }
            else if (flag.equals("exit")){
                return "exit";
            }
        }
    }

    public String kafkaGetlink2(String topicName, String groupId){
        Scanner in;

        KafkaConsumer<String, String> kafkaConsumer;
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.230.122:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        kafkaConsumer = new KafkaConsumer<>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        Scanner scanner = new Scanner(System.in);
        try {
            while(true) {
                String flag = scanner.nextLine();
                if (flag.equals("exit")){
                    return "exit";
                }
                if (flag.equals("get")){
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                        for (ConsumerRecord<String, String> record : partitionRecords) {
                            return record.value();
                        }
                        long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        kafkaConsumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                    }
                }
            }
        } finally {
            kafkaConsumer.close();
        }
    }
}
