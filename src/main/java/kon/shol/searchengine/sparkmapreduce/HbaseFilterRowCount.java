package kon.shol.searchengine.sparkmapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

public class HbaseFilterRowCount {

    private static String convertScanToString(Scan scan) {
        org.apache.hadoop.hbase.protobuf.generated.ClientProtos.Scan proto = null;
        try {
            proto = ProtobufUtil.toScan(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeBytes(proto.toByteArray());
    }


    public static void main(String[] args) {
        SparkConf con = new SparkConf().setAppName("Count").setMaster("spark://ns313900.ip-188-165-230.eu:7077");
        JavaSparkContext sc = new JavaSparkContext(con);

        Configuration conf = HBaseConfiguration.create();

        Scan scan = new Scan();
        scan.setCaching(100);
        scan.addFamily(Bytes.toBytes("data"));
        
        conf.set(TableInputFormat.INPUT_TABLE, "artinBulk2");   //Enter Table name
        conf.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        conf.set(TableInputFormat.SCAN, convertScanToString(scan));

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(
                conf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class);

        System.out.println(hBaseRDD.count());

        sc.stop();

    }
}
