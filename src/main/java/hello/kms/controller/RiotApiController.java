package hello.kms.controller;

import hello.kms.domain.*;
import hello.kms.exception.SummonerNameNotExistException;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/search/riot-api")
public class RiotApiController {
    private final RiotApiService riotApiService;

    @GetMapping("/summonerAccount")
    @ResponseBody
    public SummonerAccount summonerAccount(@RequestParam String summoner) {
        return riotApiService.getSummonerAccount(summoner);
    }


    @GetMapping("/updateSummonerInfo")
    @ResponseBody
    public List<SummonerInfo> updateSummonerInfo(@RequestParam String summoner) {
        return riotApiService.updateSummonerInfo(summoner);
    }

    @GetMapping("/summonerInfo")
    @ResponseBody
    public List<SummonerInfo> summonerInfo(@RequestParam String summoner) {
        return riotApiService.getSummonerInfo(summoner);
    }

    @GetMapping("/championMastery")
    @ResponseBody
    public List<ChampionMastery> championMastery(@RequestParam String summoner) {
        return riotApiService.getChampionMastery(summoner);
    }


    @GetMapping("/updateChampionMastery")
    @ResponseBody
    public List<ChampionMastery> updateChampionMastery(@RequestParam String summoner) {
        return riotApiService.updateChampionMastery(summoner);
    }


    @GetMapping("/recentGame")
    @ResponseBody
    public List<RecentGame> recentGame(@RequestParam String summoner) {
        return riotApiService.getRecentGame(summoner);
    }


    @GetMapping("/updateRecentGame")
    @ResponseBody
    public List<RecentGame> updateRecentGame(@RequestParam String summoner){
        return riotApiService.updateRecentGame(summoner);
    }


    @GetMapping("/rotation")
    @ResponseBody
    public List<String> rotationChampion() {
        return riotApiService.getRotationChampion();
    }
}