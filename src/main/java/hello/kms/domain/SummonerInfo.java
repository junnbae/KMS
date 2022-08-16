package hello.kms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummonerInfo {
    @JsonProperty("leagueId")
    private String leagueId;
    @JsonProperty("summonerId")
    private String summonerId;
    @JsonProperty("summonerName")
    private String summonerName;
    @JsonProperty("queueType")
    private String queueType;
    @JsonProperty("tier")
    private String tier;
    @JsonProperty("rank")
    private String rank;
    @JsonProperty("leaguePoints")
    private int leaguePoints;
    @JsonProperty("wins")
    private int wins;
    @JsonProperty("losses")
    private int loses;
    @JsonProperty("hotStreak")
    private boolean hotStreak;
    @JsonProperty("veteran")
    private boolean veteran;
    @JsonProperty("freshBlood")
    private boolean freshBlood;
    @JsonProperty("inactive")
    private boolean inactive;
}
