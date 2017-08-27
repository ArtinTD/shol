package rankpage;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

public class SparkTest {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("testApp").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //  hdfs://ns313900.ip-188-165-230.eu:9000
        JavaRDD<String> distFile = sc.textFile("hdfs://ns313900.ip-188-165-230.eu:9000/dir/hadoop/hello_world.txt");

//        String logFile = "/home/hosseinkh/Desktop/sources/spark-2.2.0-bin-hadoop2.7/README.md"; // Should be some file on your system
//        SparkSession spark = SparkSession.builder().appName("Simple Application").master("local").getOrCreate();
//        Dataset<String> logData = spark.read().textFile(logFile).cache();
//
//        long numAs = logData.filter(s -> s.contains("a")).count();
//        long numBs = logData.filter(s -> s.contains("b")).count();

//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
    }
}
