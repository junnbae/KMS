package hello.kms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.*;
import hello.kms.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static hello.kms.service.GetApiByUrl.getStringFromAPI;

@Service
@RequiredArgsConstructor
public class RiotApiService {
    private final ChampMap champIdMap;
    private final SummonerAccountRepository summonerAccountRepository;
    private final SummonerInfoRepository summonerInfoRepository;
    private final RecentGameRepository recentGameRepository;
    private final ChampionMasteryRepository championMasteryRepository;
    private final FreeChampionsRepository freeChampionsRepository;

    private final ObjectMapper objectMapper;

    String serverUrl = "https://kr.api.riotgames.com";
    @Value("${riot_api_key}")
    private String apiKey;

    public SummonerAccount getSummonerAccount(String summoner) {
        String summonerName = summoner.replace(" ", "").toLowerCase();

        return summonerAccountRepository.findByInputName(summonerName).orElseGet(() -> {
            try{
                String body = getStringFromAPI(serverUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + apiKey);
                SummonerAccount accountAPI = objectMapper.readValue(body, SummonerAccount.class);
                accountAPI.setInputName(summonerName);

                return summonerAccountRepository.save(accountAPI);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<SummonerInfo> updateSummonerInfo(String summoner){
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        String id = summonerAccount.getId();
        int pk = summonerAccount.getSummonerPk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPkOrderByQueueTypeDesc(pk);

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
            return summonerInfoRepository.findBySummonerPkOrderByQueueTypeDesc(pk);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SummonerInfo> getSummonerInfo(String summoner) {
        SummonerAccount summonerAccount = getSummonerAccount(summoner);
        int pk = summonerAccount.getSummonerPk();

        List<SummonerInfo> summonerInfoList = summonerInfoRepository.findBySummonerPkOrderByQueueTypeDesc(pk);
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

        } catch (ParseException e) {
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

        return getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuId + "/ids?start=0&count=10&api_key=" + apiKey)
                .replace("\"","")
                .replace("[", "")
                .replace("]", "")
                .split(",");
    }

    public List<RecentGame> getGameByMatchId(String name, int summonerPk, String[] matchIdList){
        Set<RecentGame> recentGameSet = new HashSet<>();

        for (String matchId : matchIdList) {
            recentGameRepository.findBySummonerPkAndMatchId(summonerPk, matchId).orElseGet(() -> {
                try {
                    String body = getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + apiKey);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

                    JSONObject info = (JSONObject) jsonObject.get("info");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                    Long timestamp = (Long) info.get("gameEndTimestamp");
                    String date = sdf.format(timestamp);

                    RecentGame recentGame = null;

                    JSONArray participants = (JSONArray) info.get("participants");
                    for (Object participant : participants) {
                        JSONObject p = (JSONObject) participant;

                        if (name.equals(p.get("summonerName"))) {
                             recentGame = RecentGame.builder()
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
                    return recentGame;

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        recentGameRepository.saveAll(recentGameSet);
        return recentGameRepository.findTop10BySummonerPkOrderByTimeStampDesc(summonerPk);
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

        List<RecentGame> recentGameList = recentGameRepository.findTop10BySummonerPkOrderByTimeStampDesc(pk);
        if (!recentGameList.isEmpty()){
            return recentGameList;
        }
        else{
            String[] gameId = updateMatchId(summoner);
            return getGameByMatchId(name, pk, gameId);
        }
    }

    public List<String> getRotationChampion() {
        if(freeChampionsRepository.findAll().isEmpty()) {
            try {
                String body = getStringFromAPI(serverUrl + "/lol/platform/v3/champion-rotations" + "?api_key=" + apiKey);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
                JSONArray freeChampionIds = (JSONArray) jsonObject.get("freeChampionIds");

                Set<FreeChampions> freeChampionsSet = new HashSet<>();

                for (Object freeChampionId : freeChampionIds) {
                    int cid = Integer.parseInt(String.valueOf(freeChampionId));
                    FreeChampions freeChampion = FreeChampions.builder()
                            .championId(cid)
                            .championName(champIdMap.getChampIdMap().get(cid))
                            .build();
                    freeChampionsSet.add(freeChampion);
                }
                freeChampionsRepository.saveAll(freeChampionsSet);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        List<String> championName = new ArrayList<>();
        List<FreeChampions> freeChampions = freeChampionsRepository.findAll();
        for(FreeChampions freeChampion : freeChampions){
            championName.add(freeChampion.getChampionName());
        }
        return championName;
    }
}
