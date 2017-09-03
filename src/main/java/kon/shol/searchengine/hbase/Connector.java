package kon.shol.searchengine.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Connector {

    private final static Logger logger = Logger.getLogger("custom");
    static Connection connection;

    private void connect(String zooKeeperIp) throws IOException {
        logger.info("Connecting to Hbase");
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zooKeeperIp);
        connection = ConnectionFactory.createConnection(configuration);
        logger.info("Connection to Hbase established");
    }

    private Connector(String zooKeeperIp) throws IOException {
        try {
            if (connection.isClosed()) {
                connect(zooKeeperIp);
            } else {
                logger.info("There is a valid connection");
            }
        } catch (NullPointerException e) {
            logger.info("Initiating Hbase Connection");
            connect(zooKeeperIp);
        }
    }

    public Connector() throws IOException {
        this("188.165.230.122:2181");
    }

    public Connection getConnection() {
        return connection;
    }

}
