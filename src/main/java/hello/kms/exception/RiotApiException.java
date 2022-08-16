package hello.kms.exception;

public class RiotApiException extends RuntimeException{
    public RiotApiException() {
        super("Can not get Riot API");
    }
}
