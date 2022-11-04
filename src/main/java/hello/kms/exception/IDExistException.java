package hello.kms.exception;

public class IDExistException extends RuntimeException{
    public IDExistException() {
        super("The ID that already exists.");
    }
}
