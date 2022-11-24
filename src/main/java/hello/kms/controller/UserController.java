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
//      https://kauth.kakao.com/oauth/authorize?client_id=85599fcffe20ab676fa22f2c8f797546&redirect_uri=https://kmslolservice.cf/login/oauth2/code/kakao&response_type=code
    }

    @GetMapping("/login/oauth2/code/naver")
    @ResponseBody
    public String naverLogin(@RequestParam String code){
        return userService.naverLogin(code);
//        https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=weeO3S29sWCykO_vmNKh&redirect_uri=https://localhost/login/oauth2/code/naver&state=
    }

    @GetMapping("/admin/adminUser")
    @ResponseBody
    public List<User> adminUser(){
        return userService.adminUser();
    }
}