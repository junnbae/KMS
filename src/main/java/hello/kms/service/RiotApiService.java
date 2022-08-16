package hello.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.RotationChampions;
import hello.kms.domain.SummonerAccount;
import hello.kms.domain.SummonerInfo;
import hello.kms.exception.RiotApiException;
import hello.kms.exception.SummonerNameNotExist;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiotApiService {
    private final ChamMap champIdMap;
    
    String serverUrl = "https://kr.api.riotgames.com";
    @Value("${riot_api_key}")
    private String apiKey;

    public SummonerAccount getSummonerAccount(HttpServletRequest request) {
        String name = request.getParameter("summoner");
        name = name.replaceAll(" ", "%20");

        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(serverUrl + "/lol/summoner/v4/summoners/by-name/" + name + "?api_key=" + apiKey);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            ObjectMapper objectMapper = new ObjectMapper();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(httpResponse);
                return objectMapper.readValue(body, SummonerAccount.class);
            } else {
                throw new SummonerNameNotExist();
            }
        } catch (Exception e) {
            throw new RiotApiException();
        }
    }
    public SummonerInfo[] getSummonerInfo(HttpServletRequest request) {
        String id = getSummonerAccount(request).getId();
        try{
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(serverUrl + "/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + apiKey);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            ObjectMapper objectMapper = new ObjectMapper();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(httpResponse);
                return objectMapper.readValue(body, SummonerInfo[].class);
            }
            throw new RiotApiException();
        }catch (Exception e){
            throw new RiotApiException();
        }
    }

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
            throw new RiotApiException();
        }catch (Exception e) {
            throw new RiotApiException();
        }
    }
}   
