package hello.kms.config;

import hello.kms.domain.ChampMap;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RiotApiConfig {
    private final ChampMap champIdMap;

    public RiotApiService riotApiService(){
        return new RiotApiService(champIdMap);
    }
}
