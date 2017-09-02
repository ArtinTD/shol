package kon.shol.searchengine.crawler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LruCache implements Cache {
    private LoadingCache<String, Boolean> lruCache = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Boolean>() {
                        @Override
                        public Boolean load(String key) {
                            return Boolean.FALSE;
                        }
                    });

    @Override
    public boolean exists(Object element) {
        if (element instanceof String)
            return lruCache.getIfPresent(element) != null;
        else
            return false;
    }

    @Override
    public void insert(Object element) {
        try {
            lruCache.get((String) element);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
