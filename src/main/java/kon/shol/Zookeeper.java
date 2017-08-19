package kon.shol;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Zookeeper implements Watcher {
    static ZooKeeper zk = null;
    static final Object mutex = new Object();

    String root;

    Zookeeper(String address)
            throws KeeperException, IOException {
        if(zk == null){
            System.out.println("Starting ZK:");
            zk = new ZooKeeper(address, 3000, this);
            System.out.println("Finished starting ZK: " + zk);
        }
    }


    @Override
    public void process(WatchedEvent watchedEvent) {

     synchronized (mutex) {
        mutex.notify();
    }
    }

    public static void main(String args[]){
        try {
            Zookeeper lrUcache= new Zookeeper("127.0.0.1:2181");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
