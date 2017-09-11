package kon.shol.searchengine;

import java.io.*;
import java.util.Properties;
public class Config {
    private Properties prop;
    private InputStream input = null;
    private String language;
    private String zkIP;
    private String brokersIP;
    private String tableName;
    private String topic;
    private int threadRatios;
    private int lruCacheTime;
    private int fetcherTimeOutError;
    private int hBaseBatchPutSize;
    private int timeLimit;
    private int sizeLimit;

    public Config() throws IOException {
        prop = new Properties();
        input = new FileInputStream("src/main/resources/config.properties");
        prop.load(input);
        setLanguage(prop.getProperty("language"));
        setZkIP(prop.getProperty("zkIP"));
        setBrokersIP(prop.getProperty("brokersIP"));
        setTableName( prop.getProperty("tableName"));
        setTopic(prop.getProperty("topic"));
        setThreadRatios(Integer.parseInt(prop.getProperty("threadRatios")));
        setLruCacheTime(Integer.parseInt(prop.getProperty("LruCacheTime")));
        setFetcherTimeOutError(Integer.parseInt(prop.getProperty("fetcherTimeOutError")));
        sethBaseBatchPutSize(Integer.parseInt(prop.getProperty("hBaseBatchPutSize")));
        setTimeLimit(Integer.parseInt(prop.getProperty("timeLimit")));
        setSizeLimit(Integer.parseInt(prop.getProperty("sizeLimit")));
    }

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getZkIP() {
        return zkIP;
    }

    public void setZkIP(String zkIP) {
        this.zkIP = zkIP;
    }

    public String getBrokersIP() {
        return brokersIP;
    }

    public void setBrokersIP(String brokersIP) {
        this.brokersIP = brokersIP;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getThreadRatios() {
        return threadRatios;
    }

    public void setThreadRatios(int threadRatios) {
        this.threadRatios = threadRatios;
    }

    public int getLruCacheTime() {
        return lruCacheTime;
    }

    public void setLruCacheTime(int lruCacheTime) {
        this.lruCacheTime = lruCacheTime;
    }

    public int getFetcherTimeOutError() {
        return fetcherTimeOutError;
    }

    public void setFetcherTimeOutError(int fetcherTimeOutError) {
        this.fetcherTimeOutError = fetcherTimeOutError;
    }

    public int gethBaseBatchPutSize() {
        return hBaseBatchPutSize;
    }

    public void sethBaseBatchPutSize(int hBaseBatchPutSize) {
        this.hBaseBatchPutSize = hBaseBatchPutSize;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public static void main(String args[]){
        try {
            Config config = new Config();
            System.out.println(config.getLanguage());
            System.out.println(config.getTopic());
            System.out.println(config.getBrokersIP());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

