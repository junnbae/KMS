package hello.kms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity(name="summonerAccount")
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int summoner_pk;

    @Column
    private String id;

    @Column
    private String accountId;
    @Column
    private String puuid;
    @Column
    private String name;
    @Column
    private int profileIconId;
    @Column
    private long revisionDate;
    @Column
    private long summonerLevel;

    @Column
    private String inputName;
}
