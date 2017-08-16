package kon.shol;


import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    public static void main(String[] args) {
        try {
            queue.put("https://en.wikipedia.org/wiki/Main_Page");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread[] threads = new Thread[8];
        for(Thread thread : threads) {
           thread= new Thread(new Crawler() {
               @Override
               public String getLink() {
                   try {
                       return queue.take();
                   } catch (InterruptedException e) {
                       return null;
                   }
               }

               @Override
               public void sendLink(String link) {
                   try {
                       queue.put(link);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           });
            thread.start();
        }
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

     }
    }


