package kon.shol.searchengine.crawler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LruCache{
    private static LoadingCache<String, Boolean> lruCache = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Boolean>() {
                        @Override
                        public Boolean load(String key) {
                            return Boolean.FALSE;
                        }
                    });

    public boolean exists(String element){
        return lruCache.getIfPresent(element) == null;
    }

    public void insert(String element){
        try {
            lruCache.get(element);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
