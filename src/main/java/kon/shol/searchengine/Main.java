package kon.shol.searchengine;

import kon.shol.searchengine.hbase.Connector;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Main {
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.Main.class);

    public static void main(String[] args) {
        try {
            new Connector();
        } catch (IOException e) {
            logger.error("Couldn't Connect to Hbase, Fatal Error");
            System.exit(0);
        }

    }
}
