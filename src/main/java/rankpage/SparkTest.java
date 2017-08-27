package rankpage;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SparkTest {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
//        SparkConf conf = new SparkConf().setAppName("testApp").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        //  hdfs://ns313900.ip-188-165-230.eu:9000
//        JavaRDD<String> distFile = sc.textFile("hdfs://ns313900.ip-188-165-230.eu:9000/dir/hadoop/hello_world.txt");


//        SparkSession spark = SparkSession.builder().appName("JavaWordCount").master("spark://ns326728.ip-188-165-235.eu:7077").getOrCreate();
//
//        JavaRDD<String> lines = spark.read().textFile("hdfs://ns326728.ip-188-165-235.eu:54310/dir/hadoop/hello_world.txt").javaRDD();
//
//        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
//
//        JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<>(s, 1));
//
//        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);
//
//        List<Tuple2<String, Integer>> output = counts.collect();
//        for (Tuple2<?,?> tuple : output) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//        }
//        spark.stop();
//    }
        String logFile = "/dir/application_log/log4j-SHOL.log.2"; // Should be some file on your system
        SparkSession spark = SparkSession.builder().appName("Simple Application").master("spark://ns326728.ip-188-165-235.eu:7077").getOrCreate();
        Dataset<String> logData = spark.read().textFile(logFile).cache();

        long numAs = logData.filter(s -> s.contains("a")).count();
        long numBs = logData.filter(s -> s.contains("b")).count();


    }
}