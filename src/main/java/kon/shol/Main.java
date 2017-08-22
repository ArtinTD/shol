package kon.shol;

import org.apache.log4j.Logger;

import java.io.IOException;

public class Main {

    static HBase hBase;
    final static Logger logger = Logger.getLogger(kon.shol.Main.class);

    public static void main(String[] args) throws InterruptedException {


//        logger.error("seda miad?!?");
//        logger.error("alo alo alo");
        logger.error("NEW SESSION");
        Producer producer = new Producer("CrawlerQueue");
        try {
            hBase = new HBase("188.165.230.122:2181", "main");
        } catch (IOException e) {
            logger.error("Couldn't connect to HBase!");
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


