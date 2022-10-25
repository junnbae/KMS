package hello.kms.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import hello.kms.domain.MostChamp;
import hello.kms.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawService;

    @CrossOrigin
    @GetMapping("/search/mostChamp")
    @ResponseBody
    public List<MostChamp> mostChamp(HttpServletRequest request) {
        return crawService.getMostChamp(request);
    }
}
