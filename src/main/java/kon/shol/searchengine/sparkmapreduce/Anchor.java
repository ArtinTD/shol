package kon.shol.searchengine.sparkmapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
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
import java.util.List;

public class Anchor {

    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("Anchor").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);

        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "artinBulk2");
        conf.set(TableOutputFormat.OUTPUT_TABLE, "artinBulk2");
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat");
        conf.set("mapreduce.output.fileoutputformat.outputdir", "/hbase");

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        JavaPairRDD<String, String> anchors = hBaseRDD.flatMapToPair(s -> {
            Result r = s._2;
            List<Tuple2<String, String>> anchor = new ArrayList<>();
            r.getFamilyMap(Bytes.toBytes("anchors")).forEach((k, v) -> {
                anchor.add(new Tuple2<>(Bytes.toString(k), Bytes.toString(v)));
            });

            return anchor.iterator();
        }).reduceByKey((k, v) -> k + " " + v);

        JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = anchors.mapToPair(s -> {
            Put put = new Put(Bytes.toBytes(s._1));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("anchors"), Bytes.toBytes(s._2));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });

        hbasePuts.saveAsNewAPIHadoopDataset(conf);

        sc.stop();

    }
}
