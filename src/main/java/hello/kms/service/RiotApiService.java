package hello.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.kms.domain.Summoner;
import hello.kms.exception.RiotApiNameException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class RiotApiService {
    String serverUrl = "https://kr.api.riotgames.com";
    @Value("${riot_api_key}")
    String apiKey;

//    private String getAccountId(HttpServletRequest request) {
//        String name = request.getParameter("summoner");
//        name = name.replaceAll(" ", "%20");
//
//        try {
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            HttpGet httpGet = new HttpGet(serverUrl + "/lol/summoner/v4/summoners/by-name/" + name + "?api_key=" + apiKey);
//            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            Summoner summoner = null;    // DTO
//            String accountId = "";
//            if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                ResponseHandler<String> handler = new BasicResponseHandler();
//                String body = handler.handleResponse(httpResponse);
//                summoner = objectMapper.readValue(body, Summoner.class);
//                accountId = summoner.getId();
//                return accountId;
//            }
//            return "Summoner is not exist";
//        } catch (Exception e) {
//            throw new RiotApiNameException();
//        }
//    }

//    public String getChampByAccountId(HttpServletRequest request){
//        String accountId = getAccountId(request);
//
//        try {
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            HttpGet httpGet = new HttpGet(serverUrl + "/lol/champion-mastery/v4/champion-masteries/by-summoner/" + accountId);
//    }
}
