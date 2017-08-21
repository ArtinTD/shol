package kon.shol;

import java.util.ArrayList;

public class PageData {

    ArrayList<String> links;
    String description;
    String title;
    String text;
    String h1h3;
    String h4h6;

    @Override
    public String toString() {
        return title;
    }
}
