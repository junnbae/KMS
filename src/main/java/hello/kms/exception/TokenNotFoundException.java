package hello.kms.exception;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException() {
        super("Token not found");
    }
}
