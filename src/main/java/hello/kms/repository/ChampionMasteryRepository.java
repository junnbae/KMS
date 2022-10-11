package hello.kms.repository;

import hello.kms.domain.ChampionMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChampionMasteryRepository extends JpaRepository<ChampionMastery, Long> {
    ChampionMastery save(ChampionMastery championMastery);
    List<ChampionMastery> findBySummonerPk(int pk);

    @Transactional
    void deleteAllBySummonerPk(int pk);
}
