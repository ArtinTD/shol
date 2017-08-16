package kon.shol;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    public static void main(String[] args) {
        try {
            queue.put("https://tinyz.us");
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

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        for (int i = 0; i <10 ; i++) {
//            arrayList.add(Integer.toString(i));
            String today = new Date().toString();
            producer.sendLink(Integer.toString(i) + "  " +today);
            if (i%2 == 0){
                System.out.println("produce :" + i);
            }
        }
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        consumer.getLink();
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException Exception");
                    }

                    while (!Consumer.arrayBlockingQueue.isEmpty()){
                        String result = (String) consumer.arrayBlockingQueue.poll();
                        System.out.println(result);
                    }
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    while (!Consumer.arrayBlockingQueue.isEmpty()){
                        String result = (String) consumer.arrayBlockingQueue.poll();
                        System.out.println(result + "     result");
                    }
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("join exception");
        }
    }
//       Here goes the Shol code.
}

