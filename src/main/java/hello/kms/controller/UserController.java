package hello.kms.controller;

import hello.kms.domain.User;
import hello.kms.dto.LoginUserForm;
import hello.kms.dto.RegisterUserForm;
import hello.kms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    @PostMapping("/checkId")
//    @ResponseBody
//    public HashMap<String, Boolean> checkId(@RequestParam String id){
//        return userService.validateDuplicateId(id);
//    }
//
//    @PostMapping("/signUp")
//    @ResponseBody
//    public HashMap<String, Boolean> signUp(@RequestBody RegisterUserForm form){
//        return userService.register(form);
//    }
//
//    @PostMapping("/login")
//    @ResponseBody
//    public String signIn(@RequestBody LoginUserForm form, HttpServletResponse response){
//        return userService.login(form);
//    }

    @GetMapping("/login/oauth2/code/kakao")
    @ResponseBody
    public String kakaoLogin(@RequestParam String code){
        return userService.kakaoLogin(code);
//        https://accounts.kakao.com/login/?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fresponse_type%3Dcode%26redirect_uri%3Dhttps%253A%252F%252Flocalhost%252Flogin%252Foauth2%252Fcode%252Fkakao%26through_account%3Dtrue%26client_id%3D43e398315e08268714887b3750a55107
    }

    @GetMapping("/login/oauth2/code/naver")
    @ResponseBody
    public String naverLogin(@RequestParam String code){
        return userService.naverLogin(code);
//        https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=I7UExJKBf_S3n8z2QDw3&redirect_uri=https://localhost/login/oauth2/code/naver&state=
    }

    @GetMapping("/admin/adminUser")
    @ResponseBody
    public List<User> adminUser(){
        return userService.adminUser();
    }
}