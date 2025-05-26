package com.eliascanalesnieto.config;

import com.eliascanalesnieto.dto.in.UserLogin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthorizationToUserLoginConverterTest {

    @Autowired
    private AuthorizationToUserLoginConverter authorizationToUserLoginConverter;

    @Test
    void hasCorrectBasicHeader(){
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes(StandardCharsets.UTF_8))
        );
        assertEquals(Optional.of(new UserLogin("username", "password")), userLogin);
    }

    @Test
    void notHasBasicHeader() {
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                "header"
        );
        assertEquals(Optional.empty(), userLogin);
    }

    @Test
    void basicHeaderIsEmpty() {
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                ""
        );
        assertEquals(Optional.empty(), userLogin);
    }

    @Test
    void basicHeaderIsNull() {
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                null
        );
        assertEquals(Optional.empty(), userLogin);
    }

    @Test
    void basicHeaderIsNotB64() {
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                "Basic username:password"
        );
        assertEquals(Optional.empty(), userLogin);
    }

    @Test
    void basicHeaderHasNotTwoValues() {
        final Optional<UserLogin> userLogin = authorizationToUserLoginConverter.convert(
                "Basic " + Base64.getEncoder().encodeToString("username".getBytes(StandardCharsets.UTF_8))
        );
        assertEquals(Optional.empty(), userLogin);
    }

}