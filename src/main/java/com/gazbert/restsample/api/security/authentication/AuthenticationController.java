package com.gazbert.restsample.api.security.authentication;

import com.gazbert.restsample.api.security.jwt.JwtUser;
import com.gazbert.restsample.api.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling initial authentication requests to obtain a JWT.
 *
 * <p>Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
@Api(tags = {"Authentication"})
@RestController
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtils jwtUtils;

  /**
   * Constructor for the controller.
   *
   * @param authenticationManager the Spring Authentication Manager.
   * @param userDetailsService the user details service for looking up users.
   * @param jwtUtils JWT utility functions.
   */
  @Autowired
  public AuthenticationController(
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      JwtUtils jwtUtils) {

    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtils = jwtUtils;
  }

  /**
   * Clients initially call this with their username/password in order to receive a JWT for use in
   * future requests.
   *
   * @param authenticationRequest the authentication request containing the client's
   *     username/password.
   * @return a JWT if the client was authenticated successfully.
   * @throws AuthenticationException if the the client was not authenticated successfully.
   */
  @PostMapping(value = "/api/token")
  public ResponseEntity<JwtAuthenticationResponse> getToken(
      @RequestBody JwtAuthenticationRequest authenticationRequest) {

    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Reload password post-security check, so we can generate the token...
    final JwtUser userDetails =
        (JwtUser) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    final String token = jwtUtils.createToken(userDetails);

    return ResponseEntity.ok(new JwtAuthenticationResponse(token));
  }

  /**
   * Clients should call this in order to refresh a JWT.
   *
   * @param request the request from the client.
   * @return the JWT with an extended expiry time if the client was authenticated, a 400 Bad Request
   *     otherwise.
   */
  @GetMapping(value = "/api/token/refresh")
  public ResponseEntity<JwtAuthenticationResponse> refreshToken(HttpServletRequest request) {

    final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final Claims claims = jwtUtils.validateTokenAndGetClaims(authorizationHeader);
    final String username = jwtUtils.getUsernameFromTokenClaims(claims);
    final JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

    if (jwtUtils.canTokenBeRefreshed(claims, new Date(user.getLastPasswordResetDate()))) {
      final String refreshedToken = jwtUtils.refreshToken(authorizationHeader);
      return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }
}
