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


    public PageGraph(String[] urls) { // this will change with hbase plugin
        getPage("A");
        getPage("B");
        getPage("C");
    }

    public List<Page> getRefrencedFrom(Page page){
        List<Page> ret = new ArrayList<>();

        switch (page.url){
            case "A":
                ret.add(getPage("C"));
                break;
            case "B":
                ret.add(getPage("A"));
                break;
            case "C":
                ret.add(getPage("A"));
                ret.add(getPage("B"));
                break;
        }

        return ret;

    }

    public Page getPage(String url){
        if ( pages.containsKey(url) )
            return pages.get(url);
        else
            return pages.put(url, new Page(url));
    }

    public void update() {
        for (Page page : pages.values()) {

            page.PRcopy = 0.5;
            for (Page referencedFrom : getRefrencedFrom(page)) {
                page.PRcopy += 0.5 * referencedFrom.getPR() / referencedFrom.referenceTo;
            }
        }

        for (Page page : pages.values()) {
            page.updatePR();
        }
    }
}
