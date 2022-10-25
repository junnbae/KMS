package hello.kms.repository;

import hello.kms.domain.SummonerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummonerAccountRepository extends JpaRepository<SummonerAccount, Long> {
    SummonerAccount findByInputName(String name);
}
