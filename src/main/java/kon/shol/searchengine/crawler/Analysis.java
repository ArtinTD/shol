package kon.shol.searchengine.crawler;

import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;


public class Analysis implements Runnable {
    ArrayBlockingQueue documentsQueue;
    Queue queue;
    Storage storage;
    Parser parser;

    private int parseErrors;

    private final static Logger logger = Logger.getLogger("custom");
    private int numCycle;

    public Analysis(Queue queue, ArrayBlockingQueue documentsQueue, Storage storage, Parser parser) {

        this.queue = queue;
        this.documentsQueue = documentsQueue;
        this.storage = storage;
        this.parser = parser;
    }

    @Override
    public void run() {
        while (true) {

            Document document;
            try {
                    document = (Document) documentsQueue.take();
            } catch (InterruptedException e) {
                continue;
            }
            try {
                parser.parse(document);
            } catch (IOException exception) {
                logger.debug("Error parsing: " + document.location());
                parseErrors++;
                continue;
            }

            storage.sendToStorage(parser.getPageData());
            try {
                queue.send(parser.getPageData().getAnchors().keySet().toArray());
            } catch (IOException e) {
                logger.fatal("Can't send to Queue");
            }
            numCycle++;
        }
    }

    public int getNumCycle(){
        return numCycle;
    }

    public void resetNumCycle(){
        numCycle = 0;
    }

    public int getParseErrors() {
        return parseErrors;
    }

}
