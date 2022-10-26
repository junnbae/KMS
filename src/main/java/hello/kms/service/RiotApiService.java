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

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiotApiService {
    private final ChampMap champIdMap;
    private final SummonerAccountRepository summonerAccountRepository;
    private final SummonerInfoRepository summonerInfoRepository;
    private final RecentGameRepository recentGameRepository;
    private final ChampionMasteryRepository championMasteryRepository;

    private final ObjectMapper objectMapper;

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
    public SummonerAccount getSummonerAccount(String summoner) {
        summoner = summoner.replace(" ", "").toLowerCase();
        SummonerAccount getSummonerAccount = summonerAccountRepository.findByInputName(summoner);

        if (getSummonerAccount != null) {
            return getSummonerAccount;
        } else {
            try {
                String body = getStringFromAPI(serverUrl + "/lol/summoner/v4/summoners/by-name/" + summoner + "?api_key=" + apiKey);
                SummonerAccount summonerAccount = objectMapper.readValue(body, SummonerAccount.class);
                summonerAccount.setInputName(summoner);

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

    public List<SummonerInfo> updateSummonerInfo(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        String id = summonerAccount.getId();
        int pk = summonerAccount.getSummonerPk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPk(pk);

        try {
            String body = getStringFromAPI("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + apiKey);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);

            SummonerInfo[] summonerInfo = new SummonerInfo[jsonArray.size()];

            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject json = (JSONObject) jsonArray.get(i);
                summonerInfo[i] = objectMapper.readValue(json.toString(), SummonerInfo.class);
                summonerInfo[i].setSummonerPk(summonerAccount.getSummonerPk());

                if(!summonerInfoList.isEmpty()){
                    summonerInfo[i].setSummonerInfoPk(summonerInfoList.get(i).getSummonerInfoPk());
                }

                summonerInfoRepository.save(summonerInfo[i]);
            }
            return summonerInfoRepository.findBySummonerPk(pk);

        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<SummonerInfo> getSummonerInfo(String summoner) {
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        int pk = summonerAccount.getSummonerPk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPk(pk);
        if(!summonerInfoList.isEmpty()){
            return summonerInfoList;
        }
        else {
            return updateSummonerInfo(summoner);
        }
    }

    public List<ChampionMastery> updateChampionMastery(String summoner) {
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        int pk = summonerAccount.getSummonerPk();
        String id = summonerAccount.getId();

        List<ChampionMastery> championMasteryList = championMasteryRepository.findBySummonerPk(pk);

        try {
            String body = getStringFromAPI("https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/" + id + "/top?api_key=" + apiKey);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);
            Set<ChampionMastery> championMasterySet = new HashSet<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                int championId = Integer.parseInt(String.valueOf(json.get("championId")));
                ChampionMastery championMastery;

                if(i < championMasteryList.size()){
                    championMastery = ChampionMastery.builder()
                            .championMasteryPk(championMasteryList.get(i).getChampionMasteryPk())
                            .championId(championId)
                            .championLevel(Integer.parseInt(String.valueOf(json.get("championLevel"))))
                            .championPoints(Integer.parseInt(String.valueOf(json.get("championPoints"))))
                            .championLevel(Integer.parseInt(String.valueOf(json.get("championLevel"))))
                            .championName(champIdMap.getChampIdMap().get(championId))
                            .summonerPk(pk)
                            .build();
                }else {
                    championMastery = ChampionMastery.builder()
                            .championId(championId)
                            .championLevel(Integer.parseInt(String.valueOf(json.get("championLevel"))))
                            .championPoints(Integer.parseInt(String.valueOf(json.get("championPoints"))))
                            .championLevel(Integer.parseInt(String.valueOf(json.get("championLevel"))))
                            .championName(champIdMap.getChampIdMap().get(championId))
                            .summonerPk(pk)
                            .build();
                }
                championMasterySet.add(championMastery);
            }
            championMasteryRepository.saveAll(championMasterySet);
            return championMasteryRepository.findBySummonerPk(pk);

        }catch (Exception e){
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<ChampionMastery> getChampionMastery(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        int pk = summonerAccount.getSummonerPk();

        List<ChampionMastery> championMasteryList = championMasteryRepository.findBySummonerPk(pk);
        if(!championMasteryList.isEmpty()){
            return championMasteryList;
        }else{
            return updateChampionMastery(summoner);
        }
    }

    public String[] updateMatchId(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        String puuId = summonerAccount.getPuuid();

        try {
            return getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuId + "/ids?start=0&count=10&api_key=" + apiKey)
                    .replace("\"","")
                    .replace("[", "")
                    .replace("]", "")
                    .split(",");

        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<RecentGame> getGameByMatchId(String name, int summonerPk, String[] matchIdList){
        Set<RecentGame> recentGameSet = new HashSet<>();

        for (String matchId : matchIdList) {
            RecentGame getRecentGame = recentGameRepository.findBySummonerPkAndMatchId(summonerPk, matchId);

            if(getRecentGame == null) {
                try {
                    String body = getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + apiKey);
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
                                    .matchId(String.valueOf(matchId))
                                    .queueId(Integer.parseInt(String.valueOf(info.get("queueId"))))
                                    .timeStamp(date)
                                    .win(Boolean.parseBoolean(String.valueOf(p.get("win"))))
                                    .champion(String.valueOf(p.get("championName")))
                                    .kill(Integer.parseInt(String.valueOf(p.get("kills"))))
                                    .death(Integer.parseInt(String.valueOf(p.get("deaths"))))
                                    .assist(Integer.parseInt(String.valueOf(p.get("assists"))))
                                    .summonerPk(summonerPk)
                                    .build();

                            recentGameSet.add(recentGame);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("e = " + e);
                    throw new RuntimeException(e);
                }
            }
        }
        recentGameRepository.saveAll(recentGameSet);
        return recentGameRepository.findBySummonerPkOrderByTimeStampDesc(summonerPk);
    }

    public List<RecentGame> updateRecentGame(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        String name = summonerAccount.getName();
        int pk = summonerAccount.getSummonerPk();
        String[] gameId = updateMatchId(summoner);
        return getGameByMatchId(name, pk, gameId);
    }

    public List<RecentGame> getRecentGame(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        String name = summonerAccount.getName();
        int pk = summonerAccount.getSummonerPk();
        List<RecentGame> recentGameList = recentGameRepository.findBySummonerPkOrderByTimeStampDesc(pk);
        if (!recentGameList.isEmpty()){
            return recentGameList;
        }
        else{
            String[] gameId = updateMatchId(summoner);
            return getGameByMatchId(name, pk, gameId);
        }

    }

    public RotationChampions getRotationChampion() {
        try {
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
