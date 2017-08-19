package kon.shol;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class LRU  extends Zookeeper{
    static LoadingCache<String, Boolean> lruCache = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Boolean>() {
                        @Override
                        public Boolean load(String key) {
                            return Boolean.FALSE;
                        }
                    });


    LRU(String address, String name) throws KeeperException, IOException, InterruptedException {
        super(address);
        this.root = name;
        // Create ZK node name
        if (zk != null) {
            Stat s = zk.exists(root, false);
            if (s == null) {
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.fromFlag(0));
            }
        }
    }

    /*boolean produce(int i) throws KeeperException, InterruptedException{
        ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;

        // Add child with value i
        b.putInt(i);
        value = b.array();
        zk.create(root + "/element", value, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateFlags.SEQUENCE);

        return true;
    }*/
}
