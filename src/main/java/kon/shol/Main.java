package kon.shol;

import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    public static void main(String[] args) {
        try {
            queue.put("https://www.google.com/");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread[] threads = new Thread[8];
        for(Thread thread : threads) {
           thread= new Thread(new Crawler());
            thread.start();
        }
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//       Here goes the Shol code.
    }
}
