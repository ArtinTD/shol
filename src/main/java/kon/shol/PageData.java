package kon.shol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageData {

    ArrayList<String> links;
    HashMap<String, String> anchors;
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
