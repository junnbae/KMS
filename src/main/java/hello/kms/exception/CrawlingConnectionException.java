package hello.kms.exception;

public class CrawlingConnectionException extends RuntimeException{
    public CrawlingConnectionException() {
        super("Crawling connection failed");
    }
}
