package kon.shol;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static kon.shol.Parser.extractLinks;
import static kon.shol.Parser.getDomain;

public abstract class Crawler implements Runnable, Kafka {
    static LoadingCache<String, Boolean> lruCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Boolean>() {
                        @Override
                        public Boolean load(String key) {
                            return Boolean.FALSE;
                        }
                    });

    public void run() {
        while (true) {
            Fetcher fetcher = new Fetcher();
            do {
                fetcher.page.link = getLink();
                String link = fetcher.page.link;
                while (lruCache.getIfPresent(getDomain(link)) != null){
                    sendLink(link);
                    fetcher.page.link = getLink();
                    link = fetcher.page.link;
                }
                try {
                    lruCache.get(getDomain(link));

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            while (!fetcher.setHTML());
            System.out.println(fetcher.page.link);
            ArrayList<String> links = extractLinks(fetcher.page.html);
            for (String link: links) {
                    sendLink(link);
            }
        }
    }

}
