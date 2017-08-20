package kon.shol;


import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Producer producer = new Producer("urls");
        Consumer consumer = new Consumer("0", "urls");

//        Thread[] threads = new Thread[60];
//        producer.sendLink("http://www.moz.com/top500");
//        for (Thread thread : threads) {
//            thread = new Thread(new Crawler() {
//                @Override
//                public String getLink() {
//                    try {
//                        return consumerQueue.take();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//
//                @Override
//                public void sendLink(String link) {
//                    try {
//                        producerQueue.put(link);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            thread.start();
//        }
//        consumer.start();
//        producer.start();
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//        consumer.join();
//        producer.join();

    }
}



