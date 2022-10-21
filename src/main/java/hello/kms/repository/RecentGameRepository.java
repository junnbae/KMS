package hello.kms.repository;

import hello.kms.domain.RecentGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentGameRepository extends JpaRepository<RecentGame, Long> {
    RecentGame save(RecentGame recentGame);
    List<RecentGame> findBySummonerPk(int pk);
    Optional<RecentGame> findBySummonerPkAndMatchId(int pk, String matchId);
}
