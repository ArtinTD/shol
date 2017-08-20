package kon.shol;

import java.util.ArrayList;

public class PageData {
    String text;
    String title;
    String description;
    ArrayList<String> links;
    String h1h3;
    String h4h6;

    @Override
    public String toString() {
        return title + " : " + text;
    }
}
