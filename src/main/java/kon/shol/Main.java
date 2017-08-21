package kon.shol;

import java.io.IOException;

public class Main {

    static HBase hBase;

    public static void main(String[] args) throws InterruptedException {

        Producer producer = new Producer("CrawlerQueue");
        try {
            hBase = new HBase("188.165.230.122:2181", "main");
        } catch (IOException e) {
            System.err.println("Couldn't connect to HBase!");
        }

        producer.sendLink("http://www.moz.com/top500");

        Thread[] threads = new Thread[60];
        for (int i = 0; i < 60; i++) {
            threads[i] = new Thread(new Asghar());
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}


