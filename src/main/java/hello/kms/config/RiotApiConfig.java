package hello.kms.config;

import hello.kms.domain.ChampMap;
import hello.kms.repository.*;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RiotApiConfig {
    private final ChampMap champIdMap;
    private final SummonerAccountRepository summonerAccountRepository;
    private final SummonerInfoRepository summonerInfoRepository;
    private final MatchIdRepository matchIdRepository;
    private final RecentGameRepository recentGameRepository;
    private final ChampionMasteryRepository championMasteryRepository;

    public RiotApiService riotApiService(){
        return new RiotApiService(champIdMap, summonerAccountRepository, summonerInfoRepository, matchIdRepository, recentGameRepository, championMasteryRepository);
    }
}
