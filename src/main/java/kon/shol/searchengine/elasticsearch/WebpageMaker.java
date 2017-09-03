package kon.shol.searchengine.elasticsearch;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class WebpageMaker {
   
   public static WebPage makeWebpage(Result r) {
      
      String url = Bytes.toString(r.getRow());
      String title = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("title")));
      String text = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("text")));
      String desc = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("description")));
      String h1h3 = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h1h3")));
      String h4h6 = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h4h6")));
      String imagesAlt = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("alt")));
      
      String anchorTexts;
      try {
         anchorTexts = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("anchors")));
      } catch (Exception expected) {
         anchorTexts = "";
      }
      
      double pageRank;
      try {
         pageRank = Bytes.toDouble(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("pagerank")));
      } catch (Exception expected) {
         pageRank = 0.15d;
      }
      
      return new WebPage(url, title, text, desc, h1h3, h4h6, imagesAlt, pageRank, anchorTexts);
   }
}
