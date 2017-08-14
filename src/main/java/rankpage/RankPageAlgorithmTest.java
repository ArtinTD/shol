package rankpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 8/14/2017.
 */
public class RankPageAlgorithmTest {

    public static void main(String[] args) {

        //Assume we get all pages!
        PageGraph pg = new PageGraph(new String[]{"A", "B", "C"});

        for (int i = 0; i < 1000; i++) { //repeat to Approximate PR
            pg.update();
        }

        for (Page page : pg.pages.values())
            System.out.println(page.url + " " + page.getPR());
    }

}