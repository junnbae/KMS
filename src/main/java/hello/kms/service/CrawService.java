//package hello.kms.service;
//
//import hello.kms.domain.MostChamp;
//import hello.kms.domain.RecentGame;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//@Service
//public class CrawService {
//    public List<MostChamp> getMostChamp(HttpServletRequest request) {
//        String summoner = request.getParameter("summoner").replaceAll(" ", "");
//        String url = "https://www.op.gg/summoners/kr/" + summoner;
//        try {
//            Document doc = Jsoup.connect(url).get();
//            Elements elementsName = doc.select(".champion-box").select(".info");
//            Elements elementsInfo = doc.select(".champion-box");
//
//            int count = 0;
//            List<MostChamp> result = new ArrayList<>();
//            Iterator<Element> elementsNameIterator = elementsName.select(".name").iterator();
//            Iterator<Element> elementsKdaIterator = elementsInfo.select(".kda").iterator();
//            Iterator<Element> elementsCountIterator = elementsInfo.select(".played").iterator();
//
//            while (elementsNameIterator.hasNext() && count < 3) {
//                MostChamp mostChamp = new MostChamp();
//                String[] WinRateCount = elementsCountIterator.next().text().split(" ");
//                int winRate = Integer.parseInt(WinRateCount[0].replaceAll("%", ""));
//                int GameCount = Integer.parseInt(WinRateCount[1]);
//
//                mostChamp.setChampion(elementsNameIterator.next().text());
//                mostChamp.setKda(Double.parseDouble(elementsKdaIterator.next().text().split(":")[0]));
//                mostChamp.setWinRate(winRate);
//                mostChamp.setCount(GameCount);
//
//                result.add(mostChamp);
//                count += 1;
//            }
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("Crawling connection failed");
//        }
//    }
//
//    public List<RecentGame> getRecentGame2(HttpServletRequest request) {
//        String summoner = request.getParameter("summoner").replaceAll(" ", "");
//        String url = "https://www.op.gg/summoners/kr/" + summoner;
//        try {
//            Document doc = Jsoup.connect(url).get();
//            Elements elementsResult = doc.select(".game");
//            Elements elementsKda = doc.select(".kda");
//            Elements elementsChampion = doc.select(".champion").select(".icon").select("img");
//
//            Iterator<Element> elementIteratorResult = elementsResult.select(".result").iterator();
//            Iterator<Element> elementIteratorTimeStamp = elementsResult.select(".time-stamp").iterator();
//            Iterator<Element> elementIteratorType = elementsResult.select(".type").iterator();
//            Iterator<Element> elementIteratorKda = elementsKda.select(".k-d-a").iterator();
//
//            int count = 0;
//            List<RecentGame> nameResult = new ArrayList<>();
//
//            while(elementIteratorResult.hasNext() && count < 10){
//                RecentGame summonerMatch = new RecentGame();
//                String[] kda = elementIteratorKda.next().text().replaceAll(" ", "").split("/");
//
//                summonerMatch.setType(elementIteratorType.next().text());
//                summonerMatch.setResult(elementIteratorResult.next().text());
//                summonerMatch.setTimeStamp(elementIteratorTimeStamp.next().text());
//                summonerMatch.setKill(Integer.parseInt(kda[0]));
//                summonerMatch.setDeath(Integer.parseInt(kda[1]));
//                summonerMatch.setAssist(Integer.parseInt(kda[2]));
//                summonerMatch.setChampion(elementsChampion.get(count).attr("alt"));
//
//                nameResult.add(summonerMatch);
//                count += 1;
//            }
//
//            return nameResult;
//        } catch (Exception e) {
//            throw new RuntimeException("Crawling connection failed");
//        }
//    }
//}
