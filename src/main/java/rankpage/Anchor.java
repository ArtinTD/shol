package rankpage;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.NavigableMap;

public class Anchor {

    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("testApp").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);
        //  hdfs://ns313900.ip-188-165-230.eu:9000
//        JavaRDD<String> distFile = sc.textFile("hdfs://ns313900.ip-188-165-230.eu:9000/dir/hadoop/hello_world.txt");


        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "testdb");
        conf.set(TableOutputFormat.OUTPUT_TABLE, "testdb");
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat");
        conf.set("mapreduce.output.fileoutputformat.outputdir", "/hbase");

        // Initialize hBase table if necessary
        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        JavaPairRDD<String, String> anchors = hBaseRDD.mapToPair(s -> {
            Result r = s._2;
            NavigableMap<byte[], byte[]> anchorsMap = r.getFamilyMap(Bytes.toBytes("anchors"));
            NavigableMap<byte[], byte[]> linksMap = r.getFamilyMap(Bytes.toBytes("links"));
            anchorsMap.forEach((k, v) -> {
                System.out.println(Bytes.toString(k) + " : " + Bytes.toString(v));
            });
            linksMap.forEach((k,v)->{
                System.out.println(Bytes.toString(k));
            });

            //             r.getValue(Bytes.toBytes("data"), Bytes.toBytes("bulk"));
            return new Tuple2<>("a", "a");
        });

    }
}
