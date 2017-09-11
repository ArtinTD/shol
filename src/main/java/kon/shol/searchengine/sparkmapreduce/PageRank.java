package kon.shol.searchengine.sparkmapreduce;

import com.google.common.collect.Iterables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.sources.In;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageRank {
    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("PageRank").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);


        Configuration conf = HBaseConfiguration.create();

        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("anchors"));

        conf.set(TableInputFormat.SCAN, convertScanToString(scan));
        conf.set(TableInputFormat.INPUT_TABLE, "amghezi");
        conf.set(TableOutputFormat.OUTPUT_TABLE, "amghezi");
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat");
        conf.set("mapreduce.output.fileoutputformat.outputdir", "/hbase");

        // Initialize hBase table if necessary
        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        JavaPairRDD<Integer, String> revertedHash = hBaseRDD.mapToPair( s -> {
            Result r = s._2;
            String row = Bytes.toString(r.getRow());
            return new Tuple2<>(row.hashCode(), row);
        }).repartition(100);

        JavaPairRDD<Integer, ArrayList<Integer>> links = hBaseRDD.mapToPair(s -> {
            Result r = s._2;
            String row = Bytes.toString(r.getRow());
            ArrayList<Integer> adj = new ArrayList<>();
            r.getFamilyMap(Bytes.toBytes("anchors")).forEach((k, v) -> {
                adj.add(Bytes.toString(k).hashCode());
            });
            return new Tuple2<>(row.hashCode() , adj);
        }).repartition(100);

        JavaPairRDD<Integer, Double> ranks = links.mapValues(rs -> 1.0);

        for (int i = 0; i < 25; i++) {
            JavaPairRDD<Integer, Double> contribs = links.join(ranks).values()
                    .flatMapToPair(s -> {
                        int urlCount = Iterables.size(s._1());
                        List<Tuple2<Integer, Double>> results = new ArrayList<>();
                        for (int n : s._1) {
                            results.add(new Tuple2<>(n, s._2() / urlCount));
                        }
                        return results.iterator();
                    });
            ranks = contribs.reduceByKey((k, v) -> k + v).mapValues(sum -> 0.15 + sum * 0.85);
        }

        JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = ranks.join(revertedHash).mapToPair(s -> {
            Put put = new Put(Bytes.toBytes(s._2._2));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("pagerank"), Bytes.toBytes(s._2._1));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });

        hbasePuts.saveAsNewAPIHadoopDataset(conf);

        sc.stop();
    }

    private static String convertScanToString(Scan scan) {
        org.apache.hadoop.hbase.protobuf.generated.ClientProtos.Scan proto = null;
        try {
            proto = ProtobufUtil.toScan(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeBytes(proto.toByteArray());
    }

}