package hello.kms.repository;

import hello.kms.domain.SummonerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummonerAccountRepository extends JpaRepository<SummonerAccount, Long> {
    Optional<SummonerAccount> findByInputName(String name);
}
