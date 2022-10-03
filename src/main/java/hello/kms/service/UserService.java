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

    public void register(RegisterUserForm form) {
        validateRegisterInput(form);
        validateDuplicateMember(form.getUserId());
        form.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));

        User user = User.builder()
                .userId(form.getUserId())
                .password(form.getPassword())
                .userName(form.getUserName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        userRepository.save(user);
    }

    private void validateDuplicateMember(String memberId){
        userRepository.findByUserId(memberId)
                .ifPresent(m -> {
                    throw new RuntimeException("The ID that already exists.");
                });
    }

    public void login(LoginUserForm form, HttpServletResponse response){
        Optional<User> findUser = userRepository.findByUserId(form.getUserId());
        if(findUser.isPresent()){
            User user = findUser.get();
            if(bCryptPasswordEncoder.matches(form.getPassword(), user.getPassword())){
                String refreshToken = null;
                try {
                    String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles(), response);
                    refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getRoles(), response);
                }catch (Exception e){
                    throw new RuntimeException("Can not set cookie");
                }

                try{
                    userRepository.save(User.builder()
                        .userPk(user.getUserPk())
                        .userId(user.getUserId())
                        .password(user.getPassword())
                        .userName(user.getUsername())
                        .roles(user.getRoles())
                        .refreshToken(refreshToken)
                        .build());
                    return;

                }catch (Exception e){
                    throw new RuntimeException("Failed to save refresh token to DB");
                }
            }else{
                throw new RuntimeException("The password is not match");
            }
        }
        throw new RuntimeException("The ID is not exist.");
    }

    public void logout(HttpServletResponse response) {
        try {
            Cookie accessCookie = new Cookie("X-AUTH-ACCESS-TOKEN", null);
            accessCookie.setMaxAge(0);
            response.addCookie(accessCookie);
            Cookie refreshCookie = new Cookie("X-AUTH-REFRESH-TOKEN", null);
            refreshCookie.setMaxAge(0);
            response.addCookie(refreshCookie);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete cookie");
        }
    }

    public Page<User> findUser(Pageable pageable){
        return userRepository.findByRoles("ROLE_USER", pageable);
    }

    public void validateRegisterInput(RegisterUserForm form){
        if(form.getUserId() == null || form.getPassword() == null || form.getUserName() == null){
            throw new RuntimeException("Input is not valid.");
        }
    }
}
