package kon.shol.searchengine.parser.exceptions;

public class UnknownLanguageException extends RuntimeException {
    public UnknownLanguageException(String url) {
        super("Unable to Recognize Language: " + url);
    }
}
