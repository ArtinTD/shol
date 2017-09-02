package kon.shol.searchengine.parser;


import java.util.ArrayList;
import java.util.HashMap;

public class PageData {

    private HashMap<String, String> anchors;
    private String description;
    private String title;
    private String text;
    private String h1h3;
    private String h4h6;
    private String url;
    private String imagesAlt;

    public HashMap<String, String> getAnchors() {
        return anchors;
    }

    public void setAnchors(HashMap<String, String> anchors) {
        this.anchors = anchors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getH1h3() {
        return h1h3;
    }

    public void setH1h3(String h1h3) {
        this.h1h3 = h1h3;
    }

    public String getH4h6() {
        return h4h6;
    }

    public void setH4h6(String h4h6) {
        this.h4h6 = h4h6;
    }

    public String getImagesAlt() {
        return imagesAlt;
    }

    public void setImagesAlt(String imagesAlt) {
        this.imagesAlt = imagesAlt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }
}
