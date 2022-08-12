package hello.kms.exception;

public class IdExistException extends RuntimeException{
    public IdExistException() {
        super("The ID that already exists.");
    }
}
