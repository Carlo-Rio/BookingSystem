package com.booking.system.v1.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {


    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // check existing sessions for this user
        UserDetails user =
                (UserDetails) authentication.getPrincipal();

        String jwt =
                jwtService.generateToken(user);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        response.getWriter().write(
                "{\"token\":\"" + jwt + "\"}"
        );
    }
}
