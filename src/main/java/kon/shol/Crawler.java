package kon.shol;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static kon.shol.Main.queue;
import static kon.shol.Parser.extractLinks;
import static kon.shol.Parser.trimLink;

public class Crawler implements Runnable, Kafka {

    public void run() {
        while (true) {
            Fetcher fetcher = new Fetcher();
            do {
                fetcher.page.link = getLink();
            }
            while (!fetcher.setHtml());
            System.out.println(fetcher.page.link  );
            Elements links = extractLinks(fetcher.page.html);
            for (Element link: links) {
                try{
                    sendLink(trimLink(link));
                }
                catch(NullPointerException ignore){

                }
            }
        }
    }

    @Override
    public String getLink() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendLink(String link) {
        try {
            try {
                queue.put(link);
            }
            catch (NullPointerException ignore){

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
