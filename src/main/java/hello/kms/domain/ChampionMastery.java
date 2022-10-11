package hello.kms.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="champion_mastery")
public class ChampionMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int champion_mastery_pk;

    @Column(name="champion_id")
    private int championId;
    @Column(name="champion_level")
    private int championLevel;
    @Column(name="champion_points")
    private int championPoints;
    @Column(name="champion_name")
    private String championName;
    @Column(name="summoner_pk")
    private int summonerPk;
}
