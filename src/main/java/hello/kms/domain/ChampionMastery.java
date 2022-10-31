package hello.kms.domain;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="champion_mastery")
public class ChampionMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int championMasteryPk;

//    @JsonProperty("championId")
    @Column(name="champion_id")
    private int championId;

//    @JsonProperty("championLevel")
    @Column(name="champion_level")
    private int championLevel;

//    @JsonProperty("championPoints")
    @Column(name="champion_points")
    private int championPoints;

    @Column(name="champion_name")
    private String championName;

    @Column(name="summoner_pk")
    private int summonerPk;
}
