package kon.shol;


import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static HBase hBase;

    public static void main(String[] args) throws InterruptedException {
        try {
            hBase = new HBase("188.165.230.122:2181", "sites");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread[] threads = new Thread[60];
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
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}


