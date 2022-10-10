package hello.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.*;
import hello.kms.exception.RiotApiException;
import hello.kms.exception.SummonerNameNotExist;
import hello.kms.repository.MatchIdRepository;
import hello.kms.repository.RecentGameRepository;
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
                JSONParser jsonParser = new JSONParser();
                JSONObject k = (JSONObject) jsonParser.parse(body);

                SummonerAccount summonerAccount = SummonerAccount.builder()
                        .accountId(String.valueOf(k.get("accountId")))
                        .puuid(String.valueOf(k.get("puuid")))
                        .id(String.valueOf(k.get("id")))
                        .name(String.valueOf(k.get("name")))
                        .profileIconId(Integer.parseInt(String.valueOf(k.get("profileIconId"))))
                        .revisionDate(Long.parseLong(String.valueOf(k.get("revisionDate"))))
                        .summonerLevel(Long.parseLong(String.valueOf(k.get("summonerLevel"))))
                        .inputName(inputName)
                        .build();

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


    public Optional<SummonerInfo> getSummonerInfo(HttpServletRequest request) {
        SummonerAccount summonerAccount = getSummonerAccount(request);
        String inputName = request.getParameter("summoner").replace(" ", "").toLowerCase();
        String id = summonerAccount.getId();

        Optional<SummonerInfo> getSummonerInfo = summonerInfoRepository.findByInputName(inputName);
        if(getSummonerInfo.isPresent()){
            return getSummonerInfo;
        }

        try {
            String body = getStringFromAPI("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + apiKey);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(body);

            SummonerInfo[] summonerInfo = new SummonerInfo[jsonArray.size()];
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject k = (JSONObject) jsonArray.get(i);

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
                        .veteran(Boolean.parseBoolean(String.valueOf(k.get("veteran"))))
                        .inactive(Boolean.parseBoolean(String.valueOf(k.get("inactive"))))
                        .hotStreak(Boolean.parseBoolean(String.valueOf(k.get("hotStreak"))))
                        .freshBlood(Boolean.parseBoolean(String.valueOf(k.get("freshBlood"))))
                        .inputName(inputName)
                        .build();

                summonerInfoRepository.save(summonerInfo[i]);
            }
            return summonerInfoRepository.findByInputName(inputName);

        }catch (Exception e){
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<MatchId> updateMatchId(HttpServletRequest request){
        SummonerAccount summonerAccount = getSummonerAccount(request);
        String puuId = summonerAccount.getPuuid();
        String name = summonerAccount.getName();

        matchIdRepository.deleteAllBySummonerName(name);

        try {
            String[] body = getStringFromAPI("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuId + "/ids?start=0&count=7&api_key=" + apiKey)
                    .replace("\"","")
                    .replace("[", "")
                    .replace("]", "")
                    .split(",");

            for (String s : body) {
                MatchId matchId = MatchId.builder()
                        .summonerName(name)
                        .matchId(String.valueOf(s))
                        .build();
                matchIdRepository.save(matchId);
            }
            return matchIdRepository.findBySummonerName(name);

        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException(e);
        }
    }

    public List<MatchId> getMatchId(HttpServletRequest request){
        String name = request.getParameter("summoner").replace(" ", "");
        List<MatchId> getMatchId = matchIdRepository.findBySummonerName(name);
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
