//package kakaoApi;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//public class kakaoApi {
//
//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
//    private String redirectUrl;
//    @Test
//    public void kakaoTokenApi(){
//        String code = "GejF-pEt4D6ykMKaN95hTjZhFSVLk7vxTGiWKCQpxHK6pPWUPsgYy1jZ802JDBof3jyUKwo9cusAAAGEdQ6ZiQ";
//        RestTemplate restTemplate = new RestTemplate();
//        String url="https://kauth.kakao.com/oauth/token?"
//                + "grant_type=authorization_code"
//                + "&client_id="+clientId
//                + "&redirect_url="+redirectUrl
//                + "&code="+code;
//
//        ResponseEntity<String> kakaoEntity = restTemplate.getForEntity(url, String.class);
//        System.out.println("kakaoEntity = " + kakaoEntity);;
//    }
//}
