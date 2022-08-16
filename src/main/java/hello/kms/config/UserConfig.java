package hello.kms.config;

import hello.kms.repository.UserRepository;
import hello.kms.security.JwtTokenProvider;
import hello.kms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public UserService userService(){
        return new UserService(userRepository, bCryptPasswordEncoder, jwtTokenProvider);
    }
}
