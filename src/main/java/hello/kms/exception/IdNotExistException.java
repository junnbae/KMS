package hello.kms.exception;

public class IdNotExistException extends RuntimeException{
    public IdNotExistException() {
        super("The ID is not exist.");
    }
}
