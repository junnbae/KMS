package hello.kms.exception;

public class JwtSetCookieException extends RuntimeException{
    public JwtSetCookieException() {
        super("Can not set cookie");
    }
}
