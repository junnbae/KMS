package hello.kms.repository;

import hello.kms.domain.SummonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummonerInfoRepository extends JpaRepository<SummonerInfo, Long> {

    List<SummonerInfo> findBySummonerPk(int pk);
}
