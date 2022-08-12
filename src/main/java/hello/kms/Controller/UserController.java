package hello.kms.Controller;

import hello.kms.Controller.form.LoginUserForm;
import hello.kms.Controller.form.RegisterUserForm;
import hello.kms.domain.User;
import hello.kms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signUp")
    public String createForm(){
        return "members/createMemberForm";
    }

    @PostMapping("/signUp")
    @ResponseBody
    public HashMap<String, Boolean> create(@RequestBody RegisterUserForm form){
        userService.register(form);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("signUpSuccess", true);
        return map;
    }

    @PostMapping("/signIn")
    @ResponseBody
    public HashMap<String, Boolean> login(@RequestBody LoginUserForm form, HttpServletResponse response){
        userService.login(form, response);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("signInSuccess", true);
        return map;
    }

    @GetMapping("/admin/users")
    @ResponseBody
    public Page<User> list(Pageable pageable){
        return userService.findUser(pageable);
    }

    @PostMapping("/signOut")
    @ResponseBody
    public HashMap<String, Boolean> logout(HttpServletResponse response){
        userService.logout(response);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("logoutSuccess", true);
        return map;
    }
}