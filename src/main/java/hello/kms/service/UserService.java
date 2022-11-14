package hello.kms.service;

import hello.kms.dto.LoginUserForm;
import hello.kms.dto.RegisterUserForm;
import hello.kms.domain.User;
import hello.kms.exception.*;
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
import java.util.HashMap;
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
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUrl;

    public HashMap<String, Boolean> register(RegisterUserForm form) {
        if(!validateDuplicateId(form.getUserId()).get("validate")){
            throw new IDExistException();
        }
        form.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));

        User user = User.builder()
                .userId(form.getUserId())
                .password(form.getPassword())
                .userName(form.getUserName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        userRepository.save(user);

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("signUpSuccess", true);
        return map;
    }

    public HashMap<String, Boolean> validateDuplicateId(String memberId){
        HashMap<String, Boolean> map = new HashMap<>();

        if(userRepository.findByUserId(memberId).isPresent()){
            map.put("validate", false);
        }else {
            map.put("validate", true);
        }
        return map;

//        userRepository.findByUserId(memberId)
//                .ifPresent(m -> {
//                    throw new IDExistException();
//                });
    }

    public String login(LoginUserForm form){
        User user = userRepository.findByUserId(form.getUserId()).orElseThrow(UserNotExistException::new);

        if(bCryptPasswordEncoder.matches(form.getPassword(), user.getPassword())){
            return jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles());
        }

        throw new PasswordWrongException();
    }

    public String kakaoLogin(String code){
        String tokenUrl="https://kauth.kakao.com/oauth/token?"
                + "grant_type=authorization_code"
                + "&client_id="+clientId
                + "&redirect_url="+redirectUrl
                + "&code="+code;

        try {
            String body = getStringFromAPI(tokenUrl);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
            String accessToken = String.valueOf(jsonObject.get("access_token"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(null, headers);
            RestTemplate restTemplate = new RestTemplate();
            String res = restTemplate.postForObject(
                    "https://kapi.kakao.com/v2/user/me",
                    request,
                    String.class
            );
            jsonObject = (JSONObject) jsonParser.parse(res);
            return String.valueOf(jsonObject);
//            return body;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> adminUser(){
        return userRepository.findAllByRoles("ROLE_USER");
    }
}
