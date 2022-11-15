package hello.kms.service;

import hello.kms.domain.User;
import hello.kms.repository.UserRepository;
import hello.kms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static hello.kms.service.GetApiByUrl.getStringFromAPI;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-url}")
    private String kakaoRedirectUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.token-url}")
    private String kakaoTokenHostUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-url}")
    private String kakaoUserInfoUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-grant-type}")
    private String kakaoAuthorizationType;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-url}")
    private String naverRedirectUrl;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.provider.naver.token-url}")
    private String naverTokenHostUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-grant-type}")
    private String naverAuthorizationType;

//    public HashMap<String, Boolean> register(RegisterUserForm form) {
//        if(!validateDuplicateId(form.getUserId()).get("validate")){
//            throw new IDExistException();
//        }
//        form.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
//
//        User user = User.builder()
//                .userId(form.getUserId())
//                .password(form.getPassword())
//                .userName(form.getUserName())
//                .roles(Collections.singletonList("ROLE_USER"))
//                .build();
//
//        userRepository.save(user);
//
//        HashMap<String, Boolean> map = new HashMap<>();
//        map.put("signUpSuccess", true);
//        return map;
//    }
//
//    public HashMap<String, Boolean> validateDuplicateId(String memberId){
//        HashMap<String, Boolean> map = new HashMap<>();
//
//        if(userRepository.findByUserId(memberId).isPresent()){
//            map.put("validate", false);
//        }else {
//            map.put("validate", true);
//        }
//        return map;
//    }
//
//    public String login(LoginUserForm form){
//        User user = userRepository.findByUserId(form.getUserId()).orElseThrow(UserNotExistException::new);
//
//        if(bCryptPasswordEncoder.matches(form.getPassword(), user.getPassword())){
//            return jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles());
//        }
//
//        throw new PasswordWrongException();
//    }

    public String getAccessToken(String code, String tokenUrl){
        try {
            String body = getStringFromAPI(tokenUrl);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(body);

            return String.valueOf(jsonObject.get("access_token"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject kakaoUserId(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.postForObject(
                kakaoUserInfoUrl,
                request,
                String.class
        );

        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(res);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String kakaoLogin(String code){
        String tokenUrl = kakaoTokenHostUrl
                + "?grant_type=" + kakaoAuthorizationType
                + "&client_id=" + kakaoClientId
                + "&redirect_url=" + kakaoRedirectUrl
                + "&code=" + code;
        String accessToken = getAccessToken(code, tokenUrl);

        JSONObject userInfo = kakaoUserId(accessToken);
        String userId = String.valueOf(userInfo.get("id"));

        User user = userRepository.findByUserId(userId).orElseGet(() ->{
            JSONObject tmp = (JSONObject) userInfo.get("properties");
            String nickname = String.valueOf(tmp.get("nickname"));

            return User.builder()
                    .userId(userId)
                    .password("")
                    .userName(nickname)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        });

        return jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles());
    }

    public String naverLogin(String code){
        String tokenUrl = naverTokenHostUrl
                + "?grant_type=" + naverAuthorizationType
                + "&client_id=" + naverClientId
                + "&redirect_url=" + naverRedirectUrl
                + "&code=" + code;
        String accessToken = getAccessToken(code, tokenUrl);
        return code;
    }

    public List<User> adminUser(){
        return userRepository.findAllByRoles("ROLE_USER");
    }
}
