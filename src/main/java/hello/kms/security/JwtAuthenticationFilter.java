package hello.kms.security;

import hello.kms.domain.User;
import hello.kms.exception.RefreshTokenCorruptedException;
import hello.kms.exception.RefreshTokenExpirationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        String accessToken = jwtTokenProvider.resolveToken((HttpServletRequest) request, "X-AUTH-ACCESS-TOKEN");
        String refreshToken = jwtTokenProvider.resolveToken((HttpServletRequest) request, "X-AUTH-REFRESH-TOKEN");

        try{
            if(refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                User user = jwtTokenProvider.findUser(refreshToken);
                if(!user.getRefreshToken().equals(refreshToken)){
                    throw new RefreshTokenCorruptedException();
                }

                if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
                    accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRoles(), (HttpServletResponse) response);
                }
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch(Exception e){
            throw new RefreshTokenExpirationException();
        }

        chain.doFilter(request, response);
    }
}
