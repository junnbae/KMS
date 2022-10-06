package hello.kms.repository;

import hello.kms.domain.SummonerAccount;
import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummonerAccountRepository extends JpaRepository<SummonerAccount, Long> {
    SummonerAccount save(SummonerAccount summonerAccount);
    Optional<SummonerAccount> findByName(String name);
}
