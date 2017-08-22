package kon.shol;


import kon.shol.KafkaClass;

import static kon.shol.Main.queue;

public class Asghar extends Crawler {

    @Override
    public void sendLink(String link) {
        try {
            queue.put(link);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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