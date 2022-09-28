package hello.kms.Controller;

import hello.kms.domain.RotationChampions;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class RiotApiController {
    private final RiotApiService riotApiService;

    @CrossOrigin
    @GetMapping("/search/riot-api/summonerAccount")
    @ResponseBody
    public JSONObject AccountByName(HttpServletRequest request) {
        return riotApiService.getSummonerAccount(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/recentGame")
    @ResponseBody
    public JSONArray recentGame(HttpServletRequest request) {
        return riotApiService.getRecentGame(request);
    }

    @CrossOrigin
    @GetMapping("/search/riot-api/rotation")
    @ResponseBody
    public RotationChampions rotationChampion() throws IOException {
        return riotApiService.getRotationChampion();
    }

    @CrossOrigin
    @GetMapping("search/riot-api/summonerInfo")
    @ResponseBody
    public JSONObject InfoByName(HttpServletRequest request) throws IOException {
        return riotApiService.getSummonerInfo(request);
    }
}