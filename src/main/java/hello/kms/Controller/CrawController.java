package hello.kms.Controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrawController {
    @GetMapping("/user/search")
    @ResponseBody
    public List<String> craw_select(HttpServletRequest request) throws IOException {
        String summoner = request.getParameter("summoner");
        String url = "https://www.op.gg/summoners/kr/" + summoner;

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".champion-box");

        int count = 0;
        List<String> nameResult = new ArrayList<>();
//        NameResult.add(elements.select(".name").text()); //챔피언 이름
        Iterator<Element> elementsIterator = elements.select(".name").iterator();
        while(elementsIterator.hasNext() && count < 3){
            nameResult.add(elementsIterator.next().text());
            count += 1;
        }

        return nameResult;
//        Map<String,Object> resultMap = new HashMap<>();
//        resultMap.put("NameResult", NameResult);

//        return resultMap;
    }
}
