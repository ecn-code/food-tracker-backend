package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.dto.in.UserLogin;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.entity.UserDynamo;
import com.eliascanalesnieto.foodtracker.exception.LoginException;
import com.eliascanalesnieto.foodtracker.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final Key key;
    private final int expirationTimeMillis;

    //TODO: Remove password
    private final EncryptService encryptService;

    public AuthorizationService(UserRepository userRepository, final AppConfig appConfig, final EncryptService encryptService) {
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(appConfig.crypto().key()));
        this.expirationTimeMillis = appConfig.crypto().expirationTimeMillis();
        this.encryptService = encryptService;
    }

    public LoginResponse createToken(final UserLogin userLogin) throws LoginException {
        log.debug("Login user: {} - {}", userLogin.username(), encryptService.encrypt(key, userLogin.password()));

        final UserDynamo userDynamo = userRepository.get(userLogin.username(), encryptService.encrypt(key, userLogin.password()))
                .orElseThrow(LoginException::new);

        /* TODO: Remove password
        userDynamo.setLastCode(null);
        userRepository.update(userDynamo);
        */

        return new LoginResponse(generateToken(userDynamo.getUsername()), userDynamo.getUsername());
    }

    public boolean validate() {
        return false;
    }

    private String generateToken(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(key)
                .compact();
    }

}
