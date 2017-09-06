package kon.shol.searchengine.kafka;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test implements Runnable {

    private static PreAnalysisQueue preAnalysisQueue;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(100);
        preAnalysisQueue = new PreAnalysisQueue();
        for (int i = 0; i < 10; i++) {
            Test test = new Test();
            executor.execute(test);
        }
    }

    @Override
    public void run() {

        int counter = 0;
        Long startTime = System.currentTimeMillis();
        while (true) {
            try {
                String string = preAnalysisQueue.get();
                counter++;
                if (counter % 100 == 0) {
                    Long time = System.currentTimeMillis() - startTime;
                    System.out.println(Thread.currentThread().getId() + " took: " + time);
                    startTime = System.currentTimeMillis();
                }
            } catch (InterruptedException ignored) { }
        }
    }
}
