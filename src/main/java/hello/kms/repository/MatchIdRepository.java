package hello.kms.repository;

import hello.kms.domain.MatchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchIdRepository extends JpaRepository<MatchId, Long> {
    MatchId save(MatchId matchId);
    List<MatchId> findBySummonerPk(int pk);
    @Transactional
    void deleteAllBySummonerPk(int pk);
}
