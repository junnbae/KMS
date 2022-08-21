package hello.kms.exception;

public class RefreshTokenCorruptedException extends RuntimeException{
    public RefreshTokenCorruptedException() {
        super("The refresh token is corrupted.");
    }
}
