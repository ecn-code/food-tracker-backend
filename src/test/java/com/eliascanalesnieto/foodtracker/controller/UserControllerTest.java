package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.MockConfig;
import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.exception.LoginException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockConfig.class)
class UserControllerTest {

    private static final String LOGIN = "/users/login";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void login() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic %s".formatted(Base64.getEncoder().encodeToString("username:password".getBytes(StandardCharsets.UTF_8))));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<LoginResponse> response = testRestTemplate.exchange(
                LOGIN,
                HttpMethod.POST,
                entity,
                LoginResponse.class
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("token")
                .isEqualTo(new LoginResponse(null, "username"));
    }

    @Test
    void errorLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic %s".formatted(Base64.getEncoder().encodeToString("username:passwor".getBytes(StandardCharsets.UTF_8))));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                LOGIN,
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals(new ErrorResponse(new LoginException().getMessage()), response.getBody());
    }

}