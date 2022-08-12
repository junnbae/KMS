package hello.kms.exception;

public class PasswordNotMatchException extends RuntimeException{
    public PasswordNotMatchException() {
        super("The password is not match");
    }
}
