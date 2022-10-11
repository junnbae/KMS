package hello.kms.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity(name="summonerInfo")
@AllArgsConstructor
@NoArgsConstructor
public class SummonerInfo {
    @Id
    private int summonerPk;

    @Column(name="league_id")
    private String leagueId;
    @Column(name="summoner_id")
    private String summonerId;
    @Column(name="summoner_name")
    private String summonerName;
    @Column(name="queue_type")
    private String queueType;
    @Column(name="tier")
    private String tier;
    @Column(name="rank")
    private String rank;
    @Column(name="league_points")
    private int leaguePoints;
    @Column(name="wins")
    private int wins;
    @Column(name="losses")
    private int losses;
    @Column(name="hot_streak")
    private boolean hotStreak;
    @Column(name="veteran")
    private boolean veteran;
    @Column(name="fresh_blood")
    private boolean freshBlood;
    @Column(name="inactive")
    private boolean inactive;
}
