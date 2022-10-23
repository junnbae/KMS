package hello.kms.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity(name="recent_game")
@AllArgsConstructor
@NoArgsConstructor
public class RecentGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recentGamePk;

    @Column(name="match_id")
    private String matchId;
    @Column(name="queue_id")
    private int queueId;
    @Column(name="time_stamp")
    private String timeStamp;
    @Column(name="win")
    private boolean win;
    @Column(name="champion")
    private String champion;
    @Column(name="kill")
    private int kill;
    @Column(name="death")
    private int death;
    @Column(name="assist")
    private int assist;

    @Column(name="summoner_pk")
    private int summonerPk;
}
