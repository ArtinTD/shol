package kon.shol.searchengine.sparkmapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class HbaseRowCount {
    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("Count").setMaster("spark://ns326728.ip-188-165-235.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);


        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "artinBulk2");   //Enter Table name
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        System.out.println(hBaseRDD.count());

        sc.stop();
    }
}
