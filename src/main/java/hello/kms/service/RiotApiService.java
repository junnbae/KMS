package hello.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.*;
import hello.kms.exception.RiotApiException;
import hello.kms.exception.SummonerNameNotExist;
import hello.kms.repository.*;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiotApiService {
    private final ChampMap champIdMap;
    private final SummonerAccountRepository summonerAccountRepository;
    private final SummonerInfoRepository summonerInfoRepository;
    private final MatchIdRepository matchIdRepository;
    private final RecentGameRepository recentGameRepository;
    private final ChampionMasteryRepository championMasteryRepository;

    String serverUrl = "https://kr.api.riotgames.com";
    @Value("${riot_api_key}")
    private String apiKey;

    public String getStringFromAPI(String url){
        try{
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 404) {
                throw new SummonerNameNotExist();
            } else if (httpResponse.getStatusLine().getStatusCode() == 403) {
                throw new RiotApiException();
            }

            ResponseHandler<String> handler = new BasicResponseHandler();

            String body = handler.handleResponse(httpResponse);
            httpClient.close();
            httpResponse.close();
            return body;

        }catch (SummonerNameNotExist e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Summoner not found");
        } catch (RiotApiException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "API Key is expired");
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }
    public SummonerAccount getSummonerAccount(HttpServletRequest request) {
        String inputName = request.getParameter("summoner").replace(" ", "").toLowerCase();
        Optional<SummonerAccount> getSummonerAccount = summonerAccountRepository.findByInputName(inputName);

        if (getSummonerAccount.isPresent()) {
            return getSummonerAccount.get();
        } else {
            try {
                String body = getStringFromAPI(serverUrl + "/lol/summoner/v4/summoners/by-name/" + inputName + "?api_key=" + apiKey);
                ObjectMapper objectMapper = new ObjectMapper();
                SummonerAccount summonerAccount = objectMapper.readValue(body, SummonerAccount.class);
                summonerAccount.setInputName(inputName);

                return summonerAccountRepository.save(summonerAccount);

            } catch (SummonerNameNotExist e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Summoner not found");
            } catch (RiotApiException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "API Key is expired");
            } catch (Exception e) {
                System.out.println("e = " + e);
                throw new RuntimeException(e);
            }
        }
    }

    public List<SummonerInfo> updateSummonerInfo(HttpServletRequest request){
        SummonerAccount summonerAccount = getSummonerAccount(request);
        String id = summonerAccount.getId();
        int pk = summonerAccount.getSummoner_pk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPk(pk);

        try {
            String body = getStringFromAPI("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + apiKey);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);
            ObjectMapper objectMapper = new ObjectMapper();

            SummonerInfo[] summonerInfo = new SummonerInfo[jsonArray.size()];

            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject json = (JSONObject) jsonArray.get(i);
                summonerInfo[i] = objectMapper.readValue(json.toString(), SummonerInfo.class);
                summonerInfo[i].setSummonerPk(summonerAccount.getSummoner_pk());

                if(!summonerInfoList.isEmpty()){
                    summonerInfo[i].setSummonerInfoPk(summonerInfoList.get(i).getSummonerInfoPk());
                }

                summonerInfoRepository.save(summonerInfo[i]);

//                System.out.println(summonerInfoList.get(i).getSummonerInfoPk() + " " + summonerInfo[i].getSummonerInfoPk());
            }
            return summonerInfoRepository.findBySummonerPk(pk);

        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<SummonerInfo> getSummonerInfo(HttpServletRequest request) {
        SummonerAccount summonerAccount = getSummonerAccount(request);
        int pk = summonerAccount.getSummoner_pk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPk(pk);
        if(!summonerInfoList.isEmpty()){
            return summonerInfoList;
        }
        else {
            return updateSummonerInfo(request);
        }
    }

    public List<ChampionMastery> updateChampionMastery(HttpServletRequest request) {
        SummonerAccount summonerAccount = getSummonerAccount(request);
        int pk = summonerAccount.getSummoner_pk();
        String id = summonerAccount.getId();

        championMasteryRepository.deleteAllBySummonerPk(pk);

        try {
            String body = getStringFromAPI("https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/" + id + "/top?api_key=" + apiKey);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);

            for (Object json : jsonArray) {
                JSONObject k = (JSONObject) json;
                int championId = Integer.parseInt(String.valueOf(k.get("championId")));

                ChampionMastery championMastery = ChampionMastery.builder()
                        .championId(championId)
                        .championLevel(Integer.parseInt(String.valueOf(k.get("championLevel"))))
                        .championPoints(Integer.parseInt(String.valueOf(k.get("championPoints"))))
                        .championLevel(Integer.parseInt(String.valueOf(k.get("championLevel"))))
                        .championName(champIdMap.getChampIdMap().get(championId))
                        .summonerPk(pk)
                        .build();
                championMasteryRepository.save(championMastery);
            }
            return championMasteryRepository.findBySummonerPk(pk);

        }catch (Exception e){
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<ChampionMastery> getChampionMastery(HttpServletRequest request){
        SummonerAccount summonerAccount = getSummonerAccount(request);
        int pk = summonerAccount.getSummoner_pk();

        List<ChampionMastery> championMasteryList = championMasteryRepository.findBySummonerPk(pk);
        if(!championMasteryList.isEmpty()){
            return championMasteryList;
        }else{
            return updateChampionMastery(request);
        }
    }

    public List<MatchId> updateMatchId(HttpServletRequest request){
        SummonerAccount summonerAccount = getSummonerAccount(request);
        String puuId = summonerAccount.getPuuid();
        int pk = summonerAccount.getSummoner_pk();

        matchIdRepository.deleteAllBySummonerPk(pk);

        try {
            String[] body = getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuId + "/ids?start=0&count=10&api_key=" + apiKey)
                    .replace("\"","")
                    .replace("[", "")
                    .replace("]", "")
                    .split(",");

            for (String s : body) {
                MatchId matchId = MatchId.builder()
                        .summonerPk(pk)
                        .matchId(String.valueOf(s))
                        .build();
                matchIdRepository.save(matchId);
            }
            return matchIdRepository.findBySummonerPk(pk);

        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<MatchId> getMatchId(HttpServletRequest request){
        int pk = getSummonerAccount(request).getSummoner_pk();
        List<MatchId> getMatchId = matchIdRepository.findBySummonerPk(pk);

        if(!getMatchId.isEmpty()){
            return getMatchId;
        }
        else {
            return updateMatchId(request);
        }
    }

    public JSONArray getGameByMatchId(String name, List<MatchId> matchIdList){
        JSONArray result = new JSONArray();
        for (MatchId matchId : matchIdList) {
            String id = matchId.getMatchId();
            Optional<RecentGame> getRecentGame = recentGameRepository.findByMatchId(id);
            if(getRecentGame.isPresent()){
                result.add(getRecentGame);
            }
            else {
                try {
                    String body = getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/" + id + "?api_key=" + apiKey);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                    JSONObject info = (JSONObject) jsonObject.get("info");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                    Long timestamp = (Long) info.get("gameEndTimestamp");
                    String date = sdf.format(timestamp);

                    JSONArray participants = (JSONArray) info.get("participants");
                    for (Object participant : participants) {
                        JSONObject p = (JSONObject) participant;

                        if (name.equals(p.get("summonerName"))) {
                            RecentGame recentGame = RecentGame.builder()
                                    .matchId(String.valueOf(id))
                                    .queueId(Integer.parseInt(String.valueOf(info.get("queueId"))))
                                    .timeStamp(date)
                                    .win(Boolean.parseBoolean(String.valueOf(p.get("win"))))
                                    .champion(String.valueOf(p.get("championName")))
                                    .kill(Integer.parseInt(String.valueOf(p.get("kills"))))
                                    .death(Integer.parseInt(String.valueOf(p.get("deaths"))))
                                    .assist(Integer.parseInt(String.valueOf(p.get("assists"))))
                                    .build();

                            recentGameRepository.save(recentGame);
                            result.add(recentGame);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("e = " + e);
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public JSONArray updateRecentGame(HttpServletRequest request){
        String name = getSummonerAccount(request).getName();
        List<MatchId> matchIdList = updateMatchId(request);
        return getGameByMatchId(name, matchIdList);
    }
    public JSONArray getRecentGame(HttpServletRequest request){
        String name = getSummonerAccount(request).getName();
        List<MatchId> matchIdList = getMatchId(request);
        return getGameByMatchId(name, matchIdList);
    }

    public RotationChampions getRotationChampion() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String body = getStringFromAPI(serverUrl + "/lol/platform/v3/champion-rotations" + "?api_key=" + apiKey);
            RotationChampions rotationChampions = objectMapper.readValue(body, RotationChampions.class);

            List<String> freeChampList = new ArrayList<>();
            for (Integer freeChampionId : rotationChampions.getFreeChampionIds()) {
                freeChampList.add(champIdMap.getChampIdMap().get(freeChampionId));
            }
            rotationChampions.setFreeChampionNames(freeChampList);

            List<String> freeChampForNewPlayersList = new ArrayList<>();
            for (Integer freeChampionIdForNewPlayer : rotationChampions.getFreeChampionIdsForNewPlayers()) {
                freeChampForNewPlayersList.add(champIdMap.getChampIdMap().get(freeChampionIdForNewPlayer));
            }
            rotationChampions.setFreeChampionNamesForNewPlayers(freeChampForNewPlayersList);

            return rotationChampions;
        }
        catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }
}
