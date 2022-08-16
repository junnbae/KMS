package hello.kms.service;

import hello.kms.domain.SummonerMatch;
import hello.kms.exception.CrawlingConnectionException;
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
    public List<String> getMostChamp(HttpServletRequest request) {
        String summoner = request.getParameter("summoner").replaceAll(" ", "%20");
        String url = "https://www.op.gg/summoners/kr/" + summoner;
        System.out.println("url = " + url);
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".champion-box");

            int count = 0;
            List<String> nameResult = new ArrayList<>();
            Iterator<Element> elementsIterator = elements.select(".name").iterator();
            while (elementsIterator.hasNext() && count < 3) {
                nameResult.add(elementsIterator.next().text());
                count += 1;
            }
            return nameResult;
        } catch (Exception e) {
            throw new CrawlingConnectionException();
        }
    }

    public List<SummonerMatch> getRecentGame(HttpServletRequest request) {
        String summoner = request.getParameter("summoner").replaceAll(" ", "%20");
        String url = "https://www.op.gg/summoners/kr/" + summoner;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elementsResult = doc.select(".game");
            Elements elementsKda = doc.select(".kda");
            Elements elementsChampion = doc.select(".champion").select(".icon").select("img");

            Iterator<Element> elementIteratorResult = elementsResult.select(".result").iterator();
            Iterator<Element> elementIteratorTimeStamp = elementsResult.select(".time-stamp").iterator();
            Iterator<Element> elementIteratorType = elementsResult.select(".type").iterator();
            Iterator<Element> elementIteratorKda = elementsKda.select(".k-d-a").iterator();

            int count = 0;
            List<SummonerMatch> nameResult = new ArrayList<>();

            while(elementIteratorResult.hasNext() && count < 10){
                SummonerMatch temp = new SummonerMatch();
                String[] kda = elementIteratorKda.next().text().replaceAll(" ", "").split("/");

                temp.setType(elementIteratorType.next().text());
                temp.setResult(elementIteratorResult.next().text());
                temp.setTimeStamp(elementIteratorTimeStamp.next().text());
                temp.setKill(Integer.parseInt(kda[0]));
                temp.setDeath(Integer.parseInt(kda[1]));
                temp.setAssist(Integer.parseInt(kda[2]));
                temp.setChampion(elementsChampion.get(count).attr("alt"));

                nameResult.add(temp);
                count += 1;
            }

            return nameResult;
        } catch (Exception e) {
            throw new CrawlingConnectionException();
        }
    }
}
