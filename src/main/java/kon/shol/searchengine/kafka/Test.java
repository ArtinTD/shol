package kon.shol.searchengine.kafka;

public class Test {
    public static void main(String[] args) {
        ElasticQueue elasticQueue = new ElasticQueue();
        elasticQueue.send("Salam");
        try {
            System.out.println(elasticQueue.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
