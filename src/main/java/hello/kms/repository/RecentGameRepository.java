package hello.kms.repository;

import hello.kms.domain.RecentGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentGameRepository extends JpaRepository<RecentGame, Long> {
    List<RecentGame> findBySummonerPkOrderByTimeStampDesc(int pk);
    RecentGame findBySummonerPkAndMatchId(int pk, String matchId);
}
