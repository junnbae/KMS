package hello.kms.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="matchId")
public class MatchId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int match_id_pk;

    @Column(name="summoner_name")
    private String summonerName;

    @Column(name="match_id")
    private String matchId;
}
