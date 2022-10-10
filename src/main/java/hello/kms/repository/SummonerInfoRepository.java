package hello.kms.repository;

import hello.kms.domain.SummonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummonerInfoRepository extends JpaRepository<SummonerInfo, Long> {
    SummonerInfo save(SummonerInfo summonerInfo);

    Optional<SummonerInfo> findByInputName(String summonerName);
}
