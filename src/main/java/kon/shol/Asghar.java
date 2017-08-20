package kon.shol;

import static kon.shol.Main.queue;

public class Asghar extends Crawler {
    Producer producer;
    Consumer consumer;

    Asghar(String topic, String groupID){
        producer = new Producer(topic);
        consumer = new Consumer(groupID, topic);
    }
    @Override
    public void sendLink(String link) {
        producer.sendLink(link);
    }

    @Override
    public String getLink() {
        try {
            return consumer.getLink();
        } catch (InterruptedException e) {
            return null;
        }
    }


}