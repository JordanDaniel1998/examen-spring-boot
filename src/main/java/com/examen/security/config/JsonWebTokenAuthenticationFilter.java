package com.examen.security.config;


import com.examen.security.service.JsonWebTokenService;
import com.examen.security.service.PersonaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private PersonaService personaService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String tokenExtraidoHeader = request.getHeader("Authorization");
        final String tokenLimpio;
        final String userEmail;

        // Si esta vacio y no empieza con Bearer permita continuar con la solicitud (ejm: vista login)
        if(StringUtils.isEmpty(tokenExtraidoHeader) ||
                !StringUtils.startsWithIgnoreCase(tokenExtraidoHeader,"Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        tokenLimpio = tokenExtraidoHeader.substring(7); // Quitamos los primeros 7 caracteres(Bearer )
        userEmail = jsonWebTokenService.extractUsername(tokenLimpio);

        // Que el email no sea null y que el context sea null
        if(Objects.nonNull(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null){

            // Envíamos la variable definida como nuestro username
            UserDetails userDetails = personaService.userDetailsService().loadUserByUsername(userEmail);
            if(jsonWebTokenService.validateToken(tokenLimpio,userDetails)){
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request,response);
    }
}
