package kon.shol;


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