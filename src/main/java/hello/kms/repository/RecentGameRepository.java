package hello.kms.repository;

import hello.kms.domain.RecentGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecentGameRepository extends JpaRepository<RecentGame, Long> {
    RecentGame save(RecentGame recentGame);
    Optional<RecentGame> findByMatchId(String matchId);
}
