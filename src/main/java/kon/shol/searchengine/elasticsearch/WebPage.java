package kon.shol.searchengine.elasticsearch;

public class WebPage {
   
   private String url;
   private String title;
   private String anchorTexts;
   private String text;
   private String description;
   private String h1h3;
   private String h4h6;
   private String imagesAlt;
   private double pageRank;
   
   public WebPage(String url, String title, String text, String description
         , String h1h3, String h4h6, String imagesAlt, double pageRank, String anchorTexts) {
      
      this.url = url;
      this.title = title;
      this.text = text;
      this.description = description;
      this.h1h3 = h1h3;
      this.h4h6 = h4h6;
      this.imagesAlt = imagesAlt;
      this.pageRank = pageRank;
      this.anchorTexts = anchorTexts;
   }
   
   public String getUrl() {
      return url;
   }
   
   public String getTitle() {
      return title;
   }
   
   public String getAnchorTexts() {
      return anchorTexts;
   }
   
   public String getText() {
      return text;
   }
   
   public String getDescription() {
      return description;
   }
   
   public String getH1h3() {
      return h1h3;
   }
   
   public String getH4h6() {
      return h4h6;
   }
   
   public String getImagesAlt() {
      return imagesAlt;
   }
   
   public double getPageRank() {
      return pageRank;
   }
}