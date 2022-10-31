package hello.kms.repository;

import hello.kms.domain.FreeChampions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeChampionsRepository extends JpaRepository<FreeChampions, Long> {
}
