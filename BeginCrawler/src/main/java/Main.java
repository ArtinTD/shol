import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
public class Main {
    static LinkedList<WebPage> queue = new LinkedList<WebPage>();
    static HashMap<WebPage, Integer> DB = new HashMap<WebPage, Integer>();
    public static void main(String args[]){
        try {
            Document doc = Jsoup.connect("http://www.uio.no/").get();
            String temp;
            Elements links;
            WebPage startPoint = new WebPage("http://www.uio.no/", doc);
            queue.add(startPoint);
            while (true){
                try {
                    doc = Jsoup.connect(queue.getFirst().link).get();
                }
                catch (Exception e){
                    System.err.println("Error: " + queue.getFirst().link);
                    queue.removeFirst();
                    continue;
                }
                System.out.println(queue.getFirst().link + " : " + queue.getLast().link.hashCode());
                links = doc.select("a");
                queue.removeFirst();
                for (Element link: links) {
                    temp= link.attr("abs:href");
                    if(link.attr("abs:href").contains("http")){
                        WebPage newPage = new WebPage(temp,null);
                        if(DB.containsKey(newPage)){
                             DB.put(newPage, DB.get(newPage) + 1);
                        }
                        else {
                            DB.put(newPage, 1);
                            queue.add(newPage);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
