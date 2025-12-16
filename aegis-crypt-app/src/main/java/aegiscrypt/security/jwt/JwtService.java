package aegiscrypt.security.jwt;

import aegiscrypt.user.domain.RoleCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    private final byte[] secret;
    private final Duration ttl;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.ttl-seconds}") long ttlSeconds
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public String issue(Long userId, String email, Set<RoleCode> roles) {
        var now = OffsetDateTime.now();
        var exp = now.plus(ttl);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roles.stream().map(Enum::name).toList())
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(exp.toInstant()))
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
    }
}
