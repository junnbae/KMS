package hello.kms.exception;

public class SaveRefreshToeknException extends RuntimeException{
    public SaveRefreshToeknException() {
        super("Failed to save refresh token to DB");
    }
}
