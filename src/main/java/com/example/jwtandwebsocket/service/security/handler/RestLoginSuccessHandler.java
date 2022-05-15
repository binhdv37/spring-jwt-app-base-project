package com.example.jwtandwebsocket.service.security.handler;

import com.example.jwtandwebsocket.service.security.model.SecurityUser;
import com.example.jwtandwebsocket.utils.token.JwtTokenFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenFactory tokenFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestLoginSuccessHandler(JwtTokenFactory tokenFactory, ObjectMapper objectMapper) {
        this.tokenFactory = tokenFactory;
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        // gen token,
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = tokenFactory.createAccessToken(securityUser);
        String refreshToken = tokenFactory.createRefreshToken(securityUser);
        Map<String, String> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("refreshToken", refreshToken);
        objectMapper.writeValue(response.getOutputStream(), resp);

        clearAuthenticationAttributes(request);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     *
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
