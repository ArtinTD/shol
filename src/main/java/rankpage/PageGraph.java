package rankpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 8/14/2017.
 */
public class PageGraph {
    Map<Page, List<Page> > referencedFrom; //this will be deleted when hbase plugin is used
    Map<String, Page> pages = new HashMap<>();

    /*
    Sample Graph:
    A -> B, C
    B -> C
    C -> A
     */


    public PageGraph() { // this will change with hbase plugin
    }

    public void addPage(Page page){
        pages.put(page.url, page);
    }


    public Page getPage(String url){
        if (pages.containsKey(url))
            return pages.get(url);
        else
            return new Page(url);
    }

    public void update() {
        for (Page page : pages.values()) {

            for (String referencedToStr : page.referenceUrls) {
                Page referencedTo = getPage(referencedToStr);
                referencedTo.PRcopy += 0.85 * page.getPR() / (double) page.referenceUrls.size();
//                page.PRcopy += 0.5 * referencedFrom.getPR() / referencedFrom.referenceTo;
            }
        }

        for (Page page : pages.values()) {
            page.updatePR();
            page.PRcopy = 0.15;
        }
    }
}
