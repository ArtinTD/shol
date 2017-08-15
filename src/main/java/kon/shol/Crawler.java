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
            while (!fetcher.setHTML());
            System.out.println(fetcher.page.link  );
            Elements links = extractLinks(fetcher.page.html);
            for (Element link: links) {
                String output = trimLink(link);
                if(!link.equals(null)){
                    sendLink(output);
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
            return " ";
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
