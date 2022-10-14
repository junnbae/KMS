package hello.kms.repository;

import hello.kms.domain.SummonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SummonerInfoRepository extends JpaRepository<SummonerInfo, Long> {
    SummonerInfo save(SummonerInfo summonerInfo);

    List<SummonerInfo> findBySummonerPk(int pk);

    @Transactional
    void deleteAllBySummonerPk(int pk);
}
