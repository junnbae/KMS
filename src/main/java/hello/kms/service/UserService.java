package hello.kms.service;

import hello.kms.Controller.form.LoginUserForm;
import hello.kms.Controller.form.RegisterUserForm;
import hello.kms.exception.*;
import hello.kms.domain.User;
import hello.kms.repository.UserRepository;
import hello.kms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public User register(RegisterUserForm form) {
        validateRegisterInput(form);
        validateDuplicateMember(form.getUserId());
        form.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));

        User user = User.builder()
                .userId(form.getUserId())
                .password(form.getPassword())
                .userName(form.getUserName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        return userRepository.save(user);
    }

    private void validateDuplicateMember(String memberId){
        userRepository.findByUserId(memberId)
                .ifPresent(m -> {
                    throw new IdExistException();
                });
    }

    public User login(LoginUserForm form, HttpServletResponse response){
        Optional<User> findUser = userRepository.findByUserId(form.getUserId());
        if(findUser.isPresent()){
            User user = findUser.get();
            if(bCryptPasswordEncoder.matches(form.getPassword(), user.getPassword())){
                String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getRoles());

                try {
//                    response.setHeader("X-AUTH-TOKEN", accessToken);
                    Cookie accessCookie = new Cookie("X-AUTH-ACCESS-TOKEN", accessToken);
                    Cookie refreshCookie = new Cookie("X-AUTH-REFRESH-TOKEN", refreshToken);
                    accessCookie.setMaxAge(30 * 60);
                    refreshCookie.setMaxAge(3 * 24 * 60 * 60);
                    response.addCookie(accessCookie);
                    response.addCookie(refreshCookie);
                }catch (Exception e){
                    throw new JwtSetCookieException();
                }
                return user;
            }else{
                throw new PasswordNotMatchException();
            }
        }
        throw new IdNotExistException();
    }

    public void logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("X-AUTH-TOKEN", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } catch (Exception e) {
            throw new JwtDelCookieException();
        }
    }

    public Page<User> findUser(Pageable pageable){
        return userRepository.findByRoles("ROLE_USER", pageable);
    }

    public void validateRegisterInput(RegisterUserForm form){
        if(form.getUserId() == null || form.getPassword() == null || form.getUserName() == null){
            throw new RegisterInputException();
        }
    }
}
