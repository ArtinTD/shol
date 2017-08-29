package kon.shol;

import java.util.HashMap;
import static kon.shol.Main.logger;

public abstract class KafkaClass {

    private static HashMap<Long, Consumer> consumerHashMap = new HashMap<>();
    private static HashMap<Long, Producer> producerHashMap = new HashMap<>();

    public static void sendLink(String link) {

        if (!producerHashMap.containsKey(Thread.currentThread().getId())) {
            producerHashMap.put(Thread.currentThread().getId(), new Producer("sajjad"));
        }
        producerHashMap.get(Thread.currentThread().getId()).sendLink(link);
    }

//    static String getLink() {
//
//        if (!consumerHashMap.containsKey(Thread.currentThread().getId())) {
//            consumerHashMap.put(Thread.currentThread().getId(), new Consumer("0", "sajjad"));
//        }
//        try {
//            return consumerHashMap.get(Thread.currentThread().getId()).getLink();
//        } catch (InterruptedException e) {
//            logger.error(e.getMessage());
//        }
//        return null;
//    }
}
