//package rankpage;
//
//import kon.shol.HBase;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.client.Table;
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
//import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
//import org.apache.hadoop.io.DoubleWritable;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//
//import java.io.IOException;
//
//
//public class MapReducePR {
//    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
//        Configuration config = HBaseConfiguration.create();
//
//        Scan scan = new Scan();
//        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
//        scan.setCacheBlocks(false);  // don't set to true for MR jobs
//// set other scan attrs
//
//        HBase hBase = new HBase("188.165.230.122:2181", "prtest");
//        Table sourceTable = hBase.getTable();
//        Table targetTable = sourceTable;
//
//        Job job = Job.getInstance(config, "Page Rank");
//        job.setJarByClass(MapReducePR.class);    // class that contains mapper
//
//
//        TableMapReduceUtil.initTableMapperJob(
//                sourceTable,      // input table
//                scan,	          // Scan instance to control CF and attribute selection
//                MyMapper.class,   // mapper class
//                ImmutableBytesWritable.class,	          // mapper output key
//                DoubleWritable.class,	          // mapper output value
//                job);
//        TableMapReduceUtil.initTableReducerJob(
//                targetTable,      // output table
//                null,             // reducer class
//                job);
//        job.setNumReduceTasks(0);
//
//        boolean b = job.waitForCompletion(true);
//        if (!b) {
//            throw new IOException("error with job!");
//        }
//    }
//}
