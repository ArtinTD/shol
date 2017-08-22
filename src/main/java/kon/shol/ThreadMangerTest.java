package kon.shol;

public class ThreadMangerTest implements Runnable{
    int numCycle =0;
    @Override
    public void run() {
        while (true){
            numCycle++;
        }
    }
}
