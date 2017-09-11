package kon.shol.searchengine.crawler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LruCache implements Cache {

    private LoadingCache<String, Boolean> lruCache = null;

    public LruCache(int cacheTime) {
        lruCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<String, Boolean>() {
                            @Override
                            public Boolean load(String key) {
                                return Boolean.FALSE;
                            }
                        });
    }

    @Override
    public boolean exists(Object element) {
        if (element instanceof String)
            return lruCache.getIfPresent(element) != null;
        else
            return false;
    }

    @Override
    public void insert(Object element) throws ExecutionException {
        lruCache.get((String) element);
    }
}
