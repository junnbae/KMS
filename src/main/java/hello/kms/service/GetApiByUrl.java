package hello.kms.service;

import hello.kms.exception.RiotApiException;
import hello.kms.exception.SummonerNameNotExistException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class GetApiByUrl {
    public static String getStringFromAPI(String url){
        try{
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 404) {
                throw new SummonerNameNotExistException();
            } else if (httpResponse.getStatusLine().getStatusCode() == 403) {
                throw new RiotApiException();
            }

            ResponseHandler<String> handler = new BasicResponseHandler();

            String body = handler.handleResponse(httpResponse);
            httpClient.close();
            httpResponse.close();
            return body;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
