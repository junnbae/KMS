package hello.kms.exception;

public class JwtDelCookieException extends RuntimeException{
    public JwtDelCookieException() {
        super("Cannot delete cookie");
    }
}
