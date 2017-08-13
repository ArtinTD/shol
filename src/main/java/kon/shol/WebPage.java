package kon.shol;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class WebPage {
    String link;
    Document html;
    long references;
    boolean exists;
    public WebPage(){
        this.references = 1;
    }

    public WebPage(String link, Document html){
        this.link = link;
        this.html = html;
        this.references =1;
    }

    @Override
    public boolean equals(Object temp){
        if(!(temp instanceof WebPage))
            return false;
        else{
            if(((WebPage) temp).link.equals(this.link)){
                this.references++;
            }
            return ((WebPage) temp).link.equals(this.link);
        }
    }

    @Override
    public int hashCode(){
        return this.link.hashCode();
    }

}
