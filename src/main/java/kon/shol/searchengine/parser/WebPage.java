package kon.shol.searchengine.parser;

import org.jsoup.nodes.Document;

public class WebPage {
    private Document html;
    private PageData pageData;
    public Document getHtml() {
        return html;
    }
    public void setHtml(Document html) {
        this.html = html;
    }

    public PageData getPageData() {
        return pageData;
    }

    public void setPageData(PageData pageData) {
        this.pageData = pageData;
    }
}
