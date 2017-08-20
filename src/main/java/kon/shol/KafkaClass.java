package kon.shol;

import java.util.HashMap;

public abstract class KafkaClass {

    private static HashMap<Long, Consumer> consumerHashMap = new HashMap<>();
    private static HashMap<Long, Producer> producerHashMap = new HashMap<>();

    public static void sendLink(String link) {
        if (!producerHashMap.containsKey(Thread.currentThread().getId())) {
            producerHashMap.put(Thread.currentThread().getId(), new Producer("urls"));
        }
        producerHashMap.get(Thread.currentThread().getId()).sendLink(link);
    }

    public static String getLink() {
        if (!consumerHashMap.containsKey(Thread.currentThread().getId())) {
            consumerHashMap.put(Thread.currentThread().getId(), new Consumer("urls", "0"));
        }
        try {
            return consumerHashMap.get(Thread.currentThread().getId()).getLink();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
