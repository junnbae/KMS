package hello.kms.Controller;

import hello.kms.domain.ChampionMastery;
import hello.kms.domain.RotationChampions;
import hello.kms.domain.SummonerAccount;
import hello.kms.domain.SummonerInfo;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
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
    public SummonerAccount accountByName(HttpServletRequest request) {
        return riotApiService.getSummonerAccount(request);
    }

    @CrossOrigin
    @GetMapping("search/riot-api/summonerInfo")
    @ResponseBody
    public List<SummonerInfo> InfoByName(HttpServletRequest request) {
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
    public JSONArray recentGame(HttpServletRequest request) {
        return riotApiService.getRecentGame(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/updateRecentGame")
    @ResponseBody
    public JSONArray updateRecentGame(HttpServletRequest request){
        return riotApiService.updateRecentGame(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/rotation")
    @ResponseBody
    public RotationChampions rotationChampion() {
        return riotApiService.getRotationChampion();
    }

}