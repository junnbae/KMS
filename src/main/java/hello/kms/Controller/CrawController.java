package hello.kms.Controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import hello.kms.domain.MostChamp;
import hello.kms.domain.SummonerMatch;
import hello.kms.service.CrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CrawController {
    private final CrawService crawService;
    @GetMapping("/search/mostChamp")
    @ResponseBody
    public List<MostChamp> mostChamp(HttpServletRequest request) {
        return crawService.getMostChamp(request);
    }

    @GetMapping("/search/recentGame")
    @ResponseBody
    public List<SummonerMatch> recentGame(HttpServletRequest request) throws IOException {
        return crawService.getRecentGame(request);
    }
}
