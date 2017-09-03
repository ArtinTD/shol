package kon.shol.searchengine.sparkmapreduce;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;

import java.util.Map;

public class EalasticMapReduceIndex {
    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("Count").setMaster("spark://ns313900.ip-188-165-230.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);


        Configuration conf = HBaseConfiguration.create();

        conf.set(TableInputFormat.INPUT_TABLE, "artinBulk2");   //Enter Table name
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");


        JavaRDD<Map<String, ?>> javaRDD = sc.parallelize( ImmutableList.of(
                ImmutableMap.of("a", "b"),
                ImmutableMap.of("c", "d"))
        );
        JavaEsSpark.saveToEs(javaRDD, "spark/docs");
    }
}
