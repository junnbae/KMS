package hello.kms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity(name="summonerAccount")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SummonerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int summoner_account_pk;

    @Column
    private String id;
    @Column
    private String account_id;
    @Column
    private String puuid;
    @Column
    private String name;
    @Column
    private int profile_iconId;
    @Column
    private long revision_date;
    @Column
    private int summoner_level;
}
