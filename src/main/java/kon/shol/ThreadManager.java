package kon.shol;

import java.util.ArrayList;

public class ThreadManager implements Runnable {

    ArrayList<Asghar> crawlers = new ArrayList<>();

    @Override
    public void run() {
        while (true) {
            crawlers.add(new Asghar());
            crawlers.get(0).numCycle;

        }
    }
}
