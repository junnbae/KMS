package hello.kms.Controller;

import hello.kms.domain.RotationChampions;
import hello.kms.domain.SummonerAccount;
import hello.kms.domain.SummonerInfo;
import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class RiotApiController {
    private final RiotApiService riotApiService;

    @CrossOrigin
    @GetMapping("/search/riot-api/summonerAccount")
    @ResponseBody
    public SummonerAccount AccountByName(HttpServletRequest request) {
        return riotApiService.getSummonerAccount(request);
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
    public SummonerInfo InfoByName(HttpServletRequest request) throws IOException {
        return riotApiService.getSummonerInfo(request)[0];
    }
}