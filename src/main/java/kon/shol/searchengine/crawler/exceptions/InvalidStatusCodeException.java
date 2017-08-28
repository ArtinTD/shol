package kon.shol.searchengine.crawler.exceptions;

import java.io.IOException;

public class InvalidStatusCodeException extends IOException {
    public InvalidStatusCodeException(int statusCode, String url){
        super("Not a valid status code: " + statusCode + " for URL: " + url);
    }
}
