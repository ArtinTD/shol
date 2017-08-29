package kon.shol;

public interface ElasticIndexer {
    void add(String url, String title, String text, String description, String h1h3, String h4h6, String imagesAlt, double pageRank);
//    void add(String newPage);
}
