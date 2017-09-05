package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Analysis;
import kon.shol.searchengine.crawler.PreAnalysis;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Monitor implements Runnable {

    private int speed = 0;
    private int sum = 0;
    private int cycles = 0;
    private int fetchErrors = 0;
    private int parseErrors = 0;
    private int invalidUrls = 0;

    private ArrayList<PreAnalysis> preAnalyses = new ArrayList<>();
    private ArrayList<Analysis> analyses =new ArrayList<>();
    private final static Logger logger = Logger.getLogger("custom");


    public void addPreAnalysis(PreAnalysis preAnalysis) {
        preAnalyses.add(preAnalysis);
    }
    public void addAnalysis(Analysis analysis){ analyses.add(analysis);}

    @Override
    public void run() {

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            logger.fatal("Monitor thread interrupted while sleeping");
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.fatal("Monitor thread interrupted while sleeping");
            }
            for (Analysis analysis : analyses) {
                parseErrors += analysis.getParseErrors();
                speed += analysis.getNumCycle();
                analysis.resetNumCycle();
            }
            for(PreAnalysis preAnalysis : preAnalyses){
                fetchErrors += preAnalysis.getFetchErrors();
                invalidUrls += preAnalysis.getInvalidUrls();
            }
            sum += speed;
            cycles += 1;
            System.out.println("");
            logger.info("Crawl Speed: " + speed);
            logger.info("Average Crawl Speed: " + sum/cycles);
            logger.info("Total Crawls: " + sum);
            logger.info("Total Fetch Errors: " + fetchErrors);
            logger.info("Total Parse Errors(none english pages and empty document: " + parseErrors);
            logger.info("Total Invalid Urls: " + invalidUrls);
            System.out.println("");

            speed = 0;
        }
    }
}
