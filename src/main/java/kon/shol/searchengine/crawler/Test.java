package kon.shol.searchengine.crawler;

import com.google.common.net.InternetDomainName;
import kon.shol.searchengine.parser.Parser;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        Parser parser = new Parser();
        try {
            System.out.println(parser.getDomain("http://www.980.sdks;dkfsdfk.com"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
