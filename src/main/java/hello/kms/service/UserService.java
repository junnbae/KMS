package hello.kms.service;

import hello.kms.dto.LoginUserForm;
import hello.kms.dto.RegisterUserForm;
import hello.kms.domain.User;
import hello.kms.exception.IDExistException;
import hello.kms.exception.PasswordWrongException;
import hello.kms.exception.UserNotExistException;
import hello.kms.repository.UserRepository;
import hello.kms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public HashMap<String, Boolean> register(RegisterUserForm form) {
        validateDuplicateMember(form.getUserId());
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

    private void validateDuplicateMember(String memberId){
        userRepository.findByUserId(memberId)
                .ifPresent(m -> {
                    throw new IDExistException();
                });
    }

    public String login(LoginUserForm form){
        Optional<User> findUser = userRepository.findByUserId(form.getUserId());
        User user = findUser.orElseThrow(UserNotExistException::new);

        if(bCryptPasswordEncoder.matches(form.getPassword(), user.getPassword())){
            return jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles());
        }

        throw new PasswordWrongException();
    }

    public List<User> adminUser(){
        return userRepository.findAllByRoles("ROLE_USER");
    }
}
