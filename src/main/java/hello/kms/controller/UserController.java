package hello.kms.controller;

import hello.kms.domain.User;
import hello.kms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public String kakaoLogin(@RequestParam String code) {
        return userService.kakaoLogin(code);
        //https://kauth.kakao.com/oauth/authorize?client_id=43e398315e08268714887b3750a55107&redirect_uri=http://localhost:8080/login/oauth2/code/kakao&response_type=code
//        https://accounts.kakao.com/login/?continue=https://kauth.kakao.com/oauth/authorize?response_type=code&redirect_uri=http://localhost:8080%/login/oauth2/code/kakao%26through_account%3Dtrue%26client_id%3D43e398315e08268714887b3750a55107    }
    }

    @GetMapping("/login/oauth2/code/naver")
    @ResponseBody
    public String naverLogin(@RequestParam String code){
        return userService.naverLogin(code);
//        https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=I7UExJKBf_S3n8z2QDw3&redirect_uri=http://localhost:8080/login/oauth2/code/naver&state=
    }

    @GetMapping("/admin/adminUser")
    @ResponseBody
    public List<User> adminUser(){
        return userService.adminUser();
    }
}