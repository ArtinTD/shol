package kon.shol;

import java.util.HashMap;

abstract class KafkaClass {

    private static HashMap<Long, Consumer> consumerHashMap = new HashMap<>();
    private static HashMap<Long, Producer> producerHashMap = new HashMap<>();

    static void sendLink(String link) {

        if (!producerHashMap.containsKey(Thread.currentThread().getId())) {
            producerHashMap.put(Thread.currentThread().getId(), new Producer("urls"));
        }
        producerHashMap.get(Thread.currentThread().getId()).sendLink(link);
    }

    static String getLink() {

        if (!consumerHashMap.containsKey(Thread.currentThread().getId())) {
            consumerHashMap.put(Thread.currentThread().getId(), new Consumer("0", "urls"));
        }
        try {
            return consumerHashMap.get(Thread.currentThread().getId()).getLink();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
