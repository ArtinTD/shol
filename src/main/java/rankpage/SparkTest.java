package rankpage;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kon.shol.HBase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import com.google.common.collect.Iterables;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.SparkSession;

public class SparkTest {
    private static final Pattern SPACE = Pattern.compile(" ");
    private static ArrayList<String> castResultToStringArrayList(Result r, String columnFamily, String cell) throws IOException {
        ArrayWritable arrayWritable = new ArrayWritable(Text.class);
        arrayWritable.readFields(
                new DataInputStream(
                        new ByteArrayInputStream(
                                r.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(cell))
                        )
                )
        );
        return fromWritable(arrayWritable);
    }
    private static ArrayList<String> fromWritable(ArrayWritable writable) {
        Writable[] writables = ((ArrayWritable) writable).get();
        ArrayList<String> list = new ArrayList<String>(writables.length);
        for (Writable wrt : writables) {
            list.add(((Text) wrt).toString());
        }
        return list;
    }

<<<<<<< HEAD
    private static class Sum implements Function2<Double, Double, Double> {
        @Override
        public Double call(Double a, Double b) {
            return a + b;
        }
    }


    public static void main(String[] args) throws IOException {
//        SparkSession spark = SparkSession.builder().appName("JavaWordCount").master("spark://ns326728.ip-188-165-235.eu:7077").getOrCreate();

        SparkConf con = new SparkConf().setAppName("testApp").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);
        //  hdfs://ns313900.ip-188-165-230.eu:9000
=======
    public static void main(String[] args) {
//        SparkConf conf = new SparkConf().setAppName("testApp").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        //  hdfs://ns313900.ip-188-165-230.eu:9000
>>>>>>> c9e08d1756713090cb83bfed35a16cd1635dc01a
//        JavaRDD<String> distFile = sc.textFile("hdfs://ns313900.ip-188-165-230.eu:9000/dir/hadoop/hello_world.txt");


        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "prtest");
        conf.set(TableOutputFormat.OUTPUT_TABLE, "prtest2");
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set("mapreduce.outputformat.class", "org.apache.hadoop.hbase.mapreduce.TableOutputFormat");
        conf.set("mapreduce.output.fileoutputformat.outputdir", "/hbase");

<<<<<<< HEAD
        // Initialize hBase table if necessary
        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);
=======
        JavaRDD<String> lines = spark.read().textFile("/dir/hadoop/hello_world.txt").javaRDD();
>>>>>>> c9e08d1756713090cb83bfed35a16cd1635dc01a

        JavaPairRDD<String, Iterable<String>> links = hBaseRDD.mapToPair(s -> {
            Result r = s._2;
            String row = Bytes.toString(r.getRow());
            ArrayList<String> adj = castResultToStringArrayList(r, "data", "bulk");
            //             r.getValue(Bytes.toBytes("data"), Bytes.toBytes("bulk"));
            return new Tuple2<>(row , adj);
        });

        // Loads all URLs with other URL(s) link to from input file and initialize ranks of them to one.
        JavaPairRDD<String, Double> ranks = links.mapValues(rs -> 1.0);

        // Calculates and updates URL ranks continuously using PageRank algorithm.
        for (int current = 0; current < 50; current++) {
            // Calculates URL contributions to the rank of other URLs.
            JavaPairRDD<String, Double> contribs = links.join(ranks).values()
                    .flatMapToPair(s -> {
                        int urlCount = Iterables.size(s._1());
                        List<Tuple2<String, Double>> results = new ArrayList<>();
                        for (String n : s._1) {
                            results.add(new Tuple2<>(n, s._2() / urlCount));
                        }
                        return results.iterator();
                    });

            // Re-calculates URL ranks based on neighbor contributions.
            ranks = contribs.reduceByKey(new Sum()).mapValues(sum -> 0.15 + sum * 0.85);
        }

        // Collects all URL ranks and dump them to console.
//        List<Tuple2<String, Double>> output = ranks.collect();
//        for (Tuple2<?,?> tuple : output) {
//
//            System.out.println(tuple._1() + " has rank: " + tuple._2() + ".");
//        }

//        Job newAPIJobConfiguration1 = Job.getInstance(conf);
//        newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "tableName");
//        newAPIJobConfiguration1.setOutputFormatClass(org.apache.hadoop.hbase.mapreduce.TableOutputFormat.class);

        JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = ranks.mapToPair(s -> {
            Put put = new Put(Bytes.toBytes(s._1));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("pagerank"), Bytes.toBytes(s._2));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });

        hbasePuts.saveAsNewAPIHadoopDataset(conf);

        sc.stop();
    }
<<<<<<< HEAD

        //        JavaRDD<String> lines = spark.read().textFile("/dir/hadoop/hello_world.txt").javaRDD();
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
=======
>>>>>>> c9e08d1756713090cb83bfed35a16cd1635dc01a
//        String logFile = "/dir/hadoop/hello_world.txt"; // Should be some file on your system
//        SparkSession spark = SparkSession.builder().appName("Simple Application").master("local").getOrCreate();
//        Dataset<String> logData = spark.read().textFile(logFile).cache();
//
//        long numAs = logData.filter(s -> s.contains("a")).count();
//        long numBs = logData.filter(s -> s.contains("b")).count();
//
//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
}
