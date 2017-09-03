package kon.shol.searchengine.sparkmapreduce;

import com.google.common.collect.Iterables;
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

public class PageRank {
    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("PageRank").setMaster("spark://ns313900.ip-188-165-230.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);


        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "artinBulk2");
        conf.set(TableOutputFormat.OUTPUT_TABLE, "artinBulk2");
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat");
        conf.set("mapreduce.output.fileoutputformat.outputdir", "/hbase");

        // Initialize hBase table if necessary
        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        JavaPairRDD<String, ArrayList<String>> links = hBaseRDD.mapToPair(s -> {
            Result r = s._2;
            String row = Bytes.toString(r.getRow());
            ArrayList<String> adj = new ArrayList<>();
            r.getFamilyMap(Bytes.toBytes("anchors")).forEach((k, v) -> {
                adj.add(Bytes.toString(k));
            });
            return new Tuple2<>(row , adj);
        }).cache();

        JavaPairRDD<String, Double> ranks = links.mapValues(rs -> 1.0);

        for (int current = 0; current < 50; current++) {
            JavaPairRDD<String, Double> contribs = links.join(ranks).values()
                    .flatMapToPair(s -> {
                        int urlCount = Iterables.size(s._1());
                        List<Tuple2<String, Double>> results = new ArrayList<>();
                        for (String n : s._1) {
                            results.add(new Tuple2<>(n, s._2() / urlCount));
                        }
                        return results.iterator();
                    });
            ranks = contribs.reduceByKey((k, v) -> k + v).mapValues(sum -> 0.15 + sum * 0.85);
        }

        JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = ranks.mapToPair(s -> {
            Put put = new Put(Bytes.toBytes(s._1));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("pagerank"), Bytes.toBytes(s._2));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });

        hbasePuts.saveAsNewAPIHadoopDataset(conf);

        sc.stop();

    }
}