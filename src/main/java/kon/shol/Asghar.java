package kon.shol;

import static kon.shol.Main.queue;
import kon.shol.KafkaClass;

public class Asghar extends Crawler {

    @Override
    public void sendLink(String link) {
        KafkaClass.sendLink(link);
    }

    @Override
    public String getLink() {
        return KafkaClass.getLink();
    }

}