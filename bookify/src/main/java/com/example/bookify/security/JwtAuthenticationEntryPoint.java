package com.example.bookify.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;


/**
 * JwtAuthenticationEntryPoint handles unauthorized access to secured endpoints.
 * It implements Spring Security's AuthenticationEntryPoint interface.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commence the authentication entry point when unauthorized access occurs.
     * Sends an HTTP error response with status code 401 (Unauthorized).
     *
     * @param req            the HTTP servlet request
     * @param res            the HTTP servlet response
     * @param authException  the authentication exception that occurred
     * @throws IOException   if an input or output exception occurs
     */
    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException authException)
            throws IOException {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
