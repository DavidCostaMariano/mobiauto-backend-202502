package com.testeTecnico.revenda.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.testeTecnico.revenda.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    private String secret;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(Objects.nonNull(token)){
            String subject = tokenService.validateToken(token);
            UserDetails userDetails = usuarioRepository.findByEmail(subject);
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            Long revendaId = jwt.getClaim("revenda_id").asLong();
            Long usuarioId = jwt.getClaim("usuarioId").asLong();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            Map<String, Object> details = new HashMap<>();
            details.put("revenda_id", revendaId);
            details.put("usuarioId", usuarioId);
            details.put("userRole", authenticationToken.getAuthorities());
            authenticationToken.setDetails(details);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        String authHeder = request.getHeader("Authorization");
        if(Objects.isNull(authHeder)) return null;
        return authHeder.replace("Bearer ", "");
    }
}
