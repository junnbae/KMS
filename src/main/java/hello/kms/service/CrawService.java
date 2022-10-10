package hello.kms.service;

import hello.kms.domain.MostChamp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CrawService {
    public List<MostChamp> getMostChamp(HttpServletRequest request) {
        String summoner = request.getParameter("summoner").replaceAll(" ", "");
        String url = "https://www.op.gg/summoners/kr/" + summoner;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elementsName = doc.select(".champion-box").select(".info");
            Elements elementsInfo = doc.select(".champion-box");

            int count = 0;
            List<MostChamp> result = new ArrayList<>();
            Iterator<Element> elementsNameIterator = elementsName.select(".name").iterator();
            Iterator<Element> elementsKdaIterator = elementsInfo.select(".kda").iterator();
            Iterator<Element> elementsCountIterator = elementsInfo.select(".played").iterator();

            while (elementsNameIterator.hasNext() && count < 3) {
                MostChamp mostChamp = new MostChamp();
                String[] WinRateCount = elementsCountIterator.next().text().split(" ");
                int winRate = Integer.parseInt(WinRateCount[0].replaceAll("%", ""));
                int GameCount = Integer.parseInt(WinRateCount[1]);

                mostChamp.setChampion(elementsNameIterator.next().text());
                mostChamp.setKda(Double.parseDouble(elementsKdaIterator.next().text().split(":")[0]));
                mostChamp.setWinRate(winRate);
                mostChamp.setCount(GameCount);

                result.add(mostChamp);
                count += 1;
            }
            return result;
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }
}
