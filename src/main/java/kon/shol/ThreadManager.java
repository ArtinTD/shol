package kon.shol;

import java.util.ArrayList;

public class ThreadManager implements Runnable {

    ArrayList<Asghar> crawlers = new ArrayList<>();
    ArrayList<Thread> threads = new ArrayList<>();
    int preSpeed = 0;
    int curSpeed = 1;

    @Override
    public void run() {
        while (curSpeed > preSpeed) {
            preSpeed = curSpeed;
            curSpeed = 0;
            crawlers.add(new Asghar());
            threads.add(new Thread(crawlers.get(crawlers.size() -1)));
            threads.get(threads.size() -1 ).start();
            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("inja" + " :  " + threads.size());
            for (int i = 0; i < crawlers.size() ; i++) {
                curSpeed += crawlers.get(i).numCycle;
                System.out.println(crawlers.get(i).numCycle);
                refresh(i);
            }


        }
        System.out.println("done");
    }

    private synchronized void refresh(int i) {
        this.crawlers.get(i).numCycle = 0;
    }
}
