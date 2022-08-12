package hello.kms.exception;

public class RegisterInputException extends RuntimeException{
    public RegisterInputException() {
        super("Input is not valid.");
    }
}
