package hello.kms.repository;

import hello.kms.domain.ChampionMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChampionMasteryRepository extends JpaRepository<ChampionMastery, Long> {
    List<ChampionMastery> findBySummonerPk(int pk);
}
