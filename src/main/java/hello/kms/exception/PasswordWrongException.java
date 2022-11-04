package hello.kms.exception;

public class PasswordWrongException extends RuntimeException{
    public PasswordWrongException(){
        super("Password is Wrong");
    }
}
