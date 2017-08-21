package kon.shol;

import java.util.ArrayList;
import java.util.List;

public class PageData {

    ArrayList<String> links;
    String description;
    String title;
    String text;
    String h1h3;
    String h4h6;
    String imagesAlt;

    @Override
    public String toString() {
        return title;
    }
}
