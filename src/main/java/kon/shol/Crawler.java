package kon.shol;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

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
            System.out.println(fetcher.page.link);
//            System.out.println(Parser.getDomain(fetcher.page.link));
            Parser.isEnglish(fetcher.page.html);
            ArrayList<String> links = extractLinks(fetcher.page.html);
            for (String link: links) {
                sendLink(link);
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
