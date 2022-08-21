package hello.kms.security;

import hello.kms.domain.User;
import hello.kms.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "DoHyungRim";

    private final UserRepository userRepository;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String memberId, List<String> roles, HttpServletResponse response){
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", roles);
        Date now = new Date();

        long tokenValidTime = 30 * 60 * 1000L;
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie accessCookie = new Cookie("X-AUTH-ACCESS-TOKEN", accessToken);
        accessCookie.setMaxAge(30 * 60);
        response.addCookie(accessCookie);
        return accessToken;
    }

    public String createRefreshToken(String memberId, List<String> roles, HttpServletResponse response){
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", roles);
        Date now = new Date();

        long tokenValidTime = 3 * 24 * 60 * 60 * 1000L;
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie refreshCookie = new Cookie("X-AUTH-REFRESH-TOKEN", refreshToken);
        refreshCookie.setMaxAge(3 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
        return refreshToken;
    }

    public Authentication getAuthentication(String token){
        User user = this.findUser(token);
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public User findUser(String token){
        String userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        Optional<User> user = userRepository.findByUserId(userId);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new UsernameNotFoundException("User not found.");
        }
    }

    public String resolveToken(HttpServletRequest request, String tokenName){
//        return request.getHeader("X-AUTH-TOKEN");
        if(request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }

    public boolean validateToken(String jwtToken){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }
}
