package hello.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.*;
import hello.kms.exception.RiotApiException;
import hello.kms.exception.SummonerNameNotExist;
import hello.kms.repository.SummonerAccountRepository;
import hello.kms.repository.SummonerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiotApiService {
    private final ChampMap champIdMap;
    private final SummonerAccountRepository summonerAccountRepository;
    private final SummonerInfoRepository summonerInfoRepository;

    String serverUrl = "https://kr.api.riotgames.com";
    @Value("${riot_api_key}")
    private String apiKey;

    public SummonerAccount getSummonerAccount(HttpServletRequest request) {
        String name = request.getParameter("summoner");
        name = name.replaceAll(" ", "%20");

        Optional<SummonerAccount> getSummonerAccount = summonerAccountRepository.findByName(name);
        if (getSummonerAccount.isPresent()) {
            return getSummonerAccount.get();
        } else {
            CloseableHttpResponse httpResponse = null;
            try {
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet httpGet = new HttpGet(serverUrl + "/lol/summoner/v4/summoners/by-name/" + name + "?api_key=" + apiKey);
                httpResponse = httpClient.execute(httpGet);

                if (httpResponse.getStatusLine().getStatusCode() == 404) {
                    throw new SummonerNameNotExist();
                } else if (httpResponse.getStatusLine().getStatusCode() == 403) {
                    throw new RiotApiException();
                }
            } catch (SummonerNameNotExist e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Summoner not found");
            } catch (RiotApiException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "API Key is expired");
            } catch (Exception e) {
                throw new RuntimeException("Cannot Get Response from Riot");
            }

            try {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(httpResponse);
                JSONParser jsonParser = new JSONParser();
                JSONObject k = (JSONObject) jsonParser.parse(body);

                SummonerAccount summonerAccount = SummonerAccount.builder()
                        .accountId(String.valueOf(k.get("accountId")))
                        .puuid(String.valueOf(k.get("puuid")))
                        .id(String.valueOf(k.get("id")))
                        .name(name)
                        .profileIconId(Integer.parseInt(String.valueOf(k.get("profileIconId"))))
                        .revisionDate(Long.parseLong(String.valueOf(k.get("revisionDate"))))
                        .summonerLevel(Long.parseLong(String.valueOf(k.get("summonerLevel"))))
                        .build();
                return summonerAccountRepository.save(summonerAccount);
            } catch (Exception e) {
                System.out.println("e = " + e);
                throw new RuntimeException("Cannot Read Value from Riot");
            }
        }
    }


    public SummonerInfo[] getSummonerInfo(HttpServletRequest request) {
//        String name = request.getParameter("summoner");

        SummonerAccount summonerAccount = getSummonerAccount(request);
        String id = summonerAccount.getId();

        CloseableHttpResponse httpResponse = null;
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + apiKey);
            httpResponse = httpClient.execute(httpGet);
        }catch (Exception e){
            throw new RuntimeException("Cannot Get Response from Riot");
        }

        try{
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(httpResponse);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);

            SummonerInfo[] summonerInfo = new SummonerInfo[jsonArray.size()];
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject k = (JSONObject) jsonArray.get(i);
                System.out.println("k = " + k);
                summonerInfo[i] = SummonerInfo.builder()
                    .wins(Integer.parseInt(String.valueOf(k.get("wins"))))
                    .summonerName(String.valueOf(k.get("summonerName")))
                    .leaguePoints(Integer.parseInt(String.valueOf(k.get("leaguePoints"))))
                    .losses(Integer.parseInt(String.valueOf(k.get("losses"))))
                    .tier(String.valueOf(k.get("tier")))
                    .leagueId(String.valueOf(k.get("leagueId")))
                    .queueType(String.valueOf(k.get("queueType")))
                    .rank(String.valueOf(k.get("rank")))
                    .summonerId(String.valueOf(k.get("summonerId")))
//                    .veteran(Boolean.parseBoolean(String.valueOf(k.get("veteran"))))
//                    .inactive(Boolean.parseBoolean(String.valueOf(k.get("inactive"))))
//                    .hotStreak(Boolean.parseBoolean(String.valueOf(k.get("hotStreak"))))
//                    .freshBlood(Boolean.parseBoolean(String.valueOf(k.get("freshBlood"))))
                    .build();

                summonerInfoRepository.save(summonerInfo[i]);
            }
            return summonerInfo;

        }catch (Exception e){
            System.out.println("e = " + e);
            throw new RuntimeException("Cannot Read Value from Riot");
        }
    }

//    public String[] getMatchId(String puuId){
//        CloseableHttpResponse httpResponse = null;
//        try {
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            HttpGet httpGet = new HttpGet("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuId + "/ids?start=0&count=7&api_key=" + apiKey);
//            httpResponse = httpClient.execute(httpGet);
//        }catch (Exception e){
//            throw new RuntimeException("Cannot Get Response from Riot");
//        }
//
//        try{
//            ResponseHandler<String> handler = new BasicResponseHandler();
//            String body = handler.handleResponse(httpResponse);
//            body = body.replaceAll("[\"]","");
//            body = body.replace("[", "");
//            body = body.replace("]", "");
//            String[] matchId = body.split(",");
//            Arrays.sort(matchId, Collections.reverseOrder());
//            return matchId;
//        }catch (Exception e){
//            throw new RuntimeException("Cannot Read Value from Riot");
//        }
//    }
//
//    public JSONArray getRecentGame(HttpServletRequest request){
//        JSONObject summonerAccount = getSummonerAccount(request);
//        String puuId = (String) summonerAccount.get("puuid");
//        String name = (String) summonerAccount.get("name");
//        String[] matchId = getMatchId(puuId);
//
//        JSONArray result = new JSONArray();
//        for (String s : matchId) {
//            CloseableHttpResponse httpResponse = null;
//            JSONObject element = new JSONObject();
//
//            try {
//                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//                HttpGet httpGet = new HttpGet("https://asia.api.riotgames.com/lol/match/v5/matches/" + s + "?api_key=" + apiKey);
//                httpResponse = httpClient.execute(httpGet);
//            } catch (Exception e) {
//                throw new RuntimeException("Cannot Get Response from Riot");
//            }
//
//            try {
//                JSONParser jsonParser = new JSONParser();
//                ResponseHandler<String> handler = new BasicResponseHandler();
//                String body = handler.handleResponse(httpResponse);
//
//                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
//
//                JSONObject info = (JSONObject) jsonObject.get("info");
//                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" , Locale.KOREA );
//                Long timestamp = (Long) info.get("gameEndTimestamp");
//                String date = sdf.format(timestamp);
//
//                element.put("queueId", info.get("queueId"));
//                element.put("timeStamp", date);
//
//                JSONArray participants = (JSONArray) info.get("participants");
//                for (Object participant : participants) {
//                    JSONObject p = (JSONObject) participant;
//
//                    if (name.equals(p.get("summonerName"))){
//                        element.put("win", p.get("win"));
//                        element.put("champion", p.get("championName"));
//                        element.put("kill", p.get("kills"));
//                        element.put("death", p.get("deaths"));
//                        element.put("assist", p.get("assists"));
//                        result.add(element);
//                        break;
//                    }
//
//                }
//            } catch (Exception e) {
//                System.out.println("e = " + e);
////                throw new RuntimeException("Cannot Read Value from Riot");
//            }
//        }
//        return result;
//    }
//
    public RotationChampions getRotationChampion() {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(serverUrl + "/lol/platform/v3/champion-rotations" + "?api_key=" + apiKey);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            ObjectMapper objectMapper = new ObjectMapper();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(httpResponse);
                RotationChampions rotationChampions = objectMapper.readValue(body, RotationChampions.class);

                List<String> freeChampList = new ArrayList<>();
                for(Integer freeChampionId : rotationChampions.getFreeChampionIds()){
                    freeChampList.add(champIdMap.getChampIdMap().get(freeChampionId));
                }
                rotationChampions.setFreeChampionNames(freeChampList);

                List<String> freeChampForNewPlayersList = new ArrayList<>();
                for(Integer freeChampionIdForNewPlayer : rotationChampions.getFreeChampionIdsForNewPlayers()){
                    freeChampForNewPlayersList.add(champIdMap.getChampIdMap().get(freeChampionIdForNewPlayer));
                }
                rotationChampions.setFreeChampionNamesForNewPlayers(freeChampForNewPlayersList);

                return rotationChampions;
            }
            throw new RuntimeException("Can not get Riot API");
        }catch (Exception e) {
            throw new RuntimeException("Cannot Get Response from Riot");
        }
    }
}
