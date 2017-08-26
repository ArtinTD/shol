package kon.shol;

import java.util.ArrayList;
import java.util.LinkedList;

public class Monitor implements Runnable {

    private int speed;

    private LinkedList<KafkaBaseCrawler> crawlers;

    void setCrawlers(LinkedList<KafkaBaseCrawler> crawlers) {
        this.crawlers = crawlers;
    }

    private LinkedList getCrawlers() {
        return crawlers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LinkedList<KafkaBaseCrawler> temp = getCrawlers();
            for (KafkaBaseCrawler crawler : temp) {
                speed += crawler.getNumCycle();
                crawler.resetNumCycle();
            }
        }
    }
}
