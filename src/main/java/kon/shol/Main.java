package kon.shol;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import kon.shol.Producer;
import kon.shol.Consumer;


public class Main {
    static HBase hBase;
    static Producer producer = new Producer("urls");
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    public static void main(String[] args) throws InterruptedException {


        try {
            hBase = new HBase("188.165.230.122:2181", "sites");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //producer.sendLink("http://www.moz.com/top500");
        producer.sendLink("http://www.alexa.com");
        Thread[] threads = new Thread[60];
        for (int i = 0; i < 60; i++) {
            threads[i] = new Thread(new Asghar("urls", "0"));
            threads[i].start();
        }
        for (Thread thread: threads) {
            thread.join();
        }
    }
}


