package hello.kms.controller;

import hello.kms.dto.LoginUserForm;
import hello.kms.dto.RegisterUserForm;
import hello.kms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signUp")
    @ResponseBody
    public JSONObject signUp(@RequestBody RegisterUserForm form){
        return (JSONObject) userService.register(form);
    }

    @PostMapping("/signIn")
    @ResponseBody
    public String signIn(@RequestBody LoginUserForm form, HttpServletResponse response){
        return userService.login(form);
    }
}