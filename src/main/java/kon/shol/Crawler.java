package kon.shol;

import static kon.shol.LRU.lruCache;
import static kon.shol.Parser.getDomain;

public abstract class Crawler implements Runnable {
    int numCycle;

    public synchronized void resetNumCycle() {
        this.numCycle = 0;
    }

    public synchronized int getNumCycle() {
        return numCycle;
    }

    public void run() {

        while (true) {

            Fetcher fetcher = new Fetcher();

            do {

                fetcher.page.link = getLink();
                String link = fetcher.page.link;

                try {
                    while (lruCache.getIfPresent(getDomain(link)) != null) {

                        sendLink(link);
                        fetcher.page.link = getLink();
                        link = fetcher.page.link;
                    }
                    lruCache.get(getDomain(link));

                } catch (Exception ignore) {
                }
            }
            while (!fetcher.setHTML());

            for (String link : fetcher.page.pageData.links) {
                sendLink(link);
            }
            numCycle++;
        }
    }

    abstract void sendLink(String link);

    abstract String getLink();


}
