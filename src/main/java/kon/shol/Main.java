package kon.shol;


import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static HBase hBase;
    static LinkedBlockingQueue<String> consumerQueue = new LinkedBlockingQueue<String>();
    static LinkedBlockingQueue<String> producerQueue = new LinkedBlockingQueue<String>();

    public static void main(String[] args) throws InterruptedException {
        try {
            hBase = new HBase("188.165.230.122:2181", "sites");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread[] threads = new Thread[60];
        Thread consumer = new Thread(new Consumer("0", "Crawler"));
        Thread producer = new Thread(new Producer());
        consumerQueue.put("http://www.moz.com/top500");
        for (Thread thread : threads) {
            thread = new Thread(new Crawler() {
                @Override
                public String getLink() {
                    try {
                        return consumerQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void sendLink(String link) {
                    try {
                        producerQueue.put(link);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        consumer.start();
        producer.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        consumer.join();
        producer.join();
    }
}


