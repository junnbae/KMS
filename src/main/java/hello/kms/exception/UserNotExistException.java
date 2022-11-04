package hello.kms.exception;

public class UserNotExistException extends RuntimeException{
    public UserNotExistException(){
        super("User does not exist");
    }
}
