package hello.kms.Controller;

import hello.kms.domain.*;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RiotApiController {
    private final RiotApiService riotApiService;

    @CrossOrigin
    @GetMapping("/search/riot-api/summonerAccount")
    @ResponseBody
    public SummonerAccount summonerAccount(HttpServletRequest request) {
        return riotApiService.getSummonerAccount(request);
    }


    @CrossOrigin
    @GetMapping("search/riot-api/updateSummonerInfo")
    @ResponseBody
    public List<SummonerInfo> updateSummonerInfo(HttpServletRequest request) {
        return riotApiService.updateSummonerInfo(request);
    }
    @CrossOrigin
    @GetMapping("search/riot-api/summonerInfo")
    @ResponseBody
    public List<SummonerInfo> summonerInfo(HttpServletRequest request) {
        return riotApiService.getSummonerInfo(request);
    }

    @CrossOrigin
        @GetMapping("/search/riot-api/championMastery")
    @ResponseBody
    public List<ChampionMastery> championMastery(HttpServletRequest request) {
        return riotApiService.getChampionMastery(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/updateChampionMastery")
    @ResponseBody
    public List<ChampionMastery> updateChampionMastery(HttpServletRequest request) {
        return riotApiService.updateChampionMastery(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/recentGame")
    @ResponseBody
    public List<RecentGame> recentGame(HttpServletRequest request) {
        return riotApiService.getRecentGame(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/updateRecentGame")
    @ResponseBody
    public List<RecentGame> updateRecentGame(HttpServletRequest request){
        return riotApiService.updateRecentGame(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/rotation")
    @ResponseBody
    public RotationChampions rotationChampion() {
        return riotApiService.getRotationChampion();
    }

}