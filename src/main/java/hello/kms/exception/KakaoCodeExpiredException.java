package hello.kms.exception;

public class KakaoCodeExpiredException extends RuntimeException{
    public KakaoCodeExpiredException(){
        super("Kakao Code is Expired");
    }
}
