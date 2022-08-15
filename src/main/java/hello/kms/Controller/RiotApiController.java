package hello.kms.Controller;

import hello.kms.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//@Controller
//@RequiredArgsConstructor
//public class RiotApiController {
//    private final RiotApiService riotApiService;
//
//    @GetMapping("/searchzz")
//    @ResponseBody
//    public String getChampGame(HttpServletRequest request) {
//        return riotApiService.getChampByAccountId(request);
//    }
//}