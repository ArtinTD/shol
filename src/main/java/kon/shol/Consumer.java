package kon.shol;

import org.apache.kafka.clients.consumer.*;

import java.util.*;

class Consumer {

    private KafkaConsumer<String, String> consumer;

    Consumer(String groupID, String topic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092,188.165.230.122:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("acks", "all");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    void getLink() throws InterruptedException {

        ConsumerRecords<String, String> records = consumer.poll(10000);
        if (records.isEmpty()) {
            System.out.println("empty");
        } else {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received message: " + record.value() + ", Partition: "
                        + record.partition() + ", Offset: " + record.offset() + ", by ThreadID: "
                        + Thread.currentThread().getId());
            }
        }
    }

//    public void run(){
//        while (true){
//            try {
//                getLink();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
