package hello.kms.exception;

public class RefreshTokenExpirationException extends RuntimeException{
    public RefreshTokenExpirationException() {
        super("Refresh Token has expired");
    }
}
