package hello.kms.exception;

public class SummonerNameNotExistException extends RuntimeException{
    public SummonerNameNotExistException(){
        super("Summoner is Not exist");
    }
}
