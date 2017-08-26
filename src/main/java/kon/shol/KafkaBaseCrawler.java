package kon.shol;


import static kon.shol.Main.queue;

public class KafkaBaseCrawler extends Crawler {

    @Override
    public void sendLink(String link) {
        KafkaClass.sendLink(link);
    }

    @Override
    public String getLink() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}