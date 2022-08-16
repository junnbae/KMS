package hello.kms.exception;

public class SummonerNameNotExist extends RuntimeException{
    public SummonerNameNotExist(){
        super("Summoner is Not exist");
    }
}
