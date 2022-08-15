package hello.kms.exception;

public class RiotApiNameException extends RuntimeException{
    public RiotApiNameException() {
        super("Can not get Riot API by Summoner name");
    }
}
