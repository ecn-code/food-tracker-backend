package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.config.MockConfig;
import com.eliascanalesnieto.foodtracker.dto.in.UnitRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.dto.out.UnitResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockConfig.class)
class UnitControllerTest {

    private static final String LOGIN = "/users/login";
    private static final String UNITS = "/units";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppConfig appConfig;

    @Test
    void getAll() {
        ResponseEntity<List<UnitResponse>> response = testRestTemplate.exchange(
                UNITS,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(
                        List.of(
                                new UnitResponse("1", "kg", "kilogram"),
                                new UnitResponse("5", "g", "gram")
                        )
                );
    }

    @Test
    void getOne() {
        final String id = "1";
        ResponseEntity<UnitResponse> response = testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(new UnitResponse(id, "kg", "kilogram"));
    }

    @Test
    void getOneDoesNotExist() {
        final String id = "8";
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void create() {
        final String kg = "kg";
        final String kilogram = "kilogram";

        ResponseEntity<UnitResponse> response = testRestTemplate.exchange(
                UNITS,
                HttpMethod.POST,
                login(new UnitRequest(null, kg, kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo( new UnitResponse(null, kg, kilogram));

        testRestTemplate.exchange(
                UNITS + "/" + response.getBody().id(),
                HttpMethod.DELETE,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void createWithId() {
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS,
                HttpMethod.POST,
                login(new UnitRequest("55", "kg", "kilogram")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    void createWithoutShortName() {
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS,
                HttpMethod.POST,
                login(new UnitRequest("55", "kg", "kilogram")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void update() {
        final String id = "1";
        final String kg = "2kg";
        final String kilogram = "kilogram";

        ResponseEntity<UnitResponse> response = testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.PUT,
                login(new UnitRequest(id, kg, kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo( new UnitResponse(id, kg, kilogram));

        testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.PUT,
                login(new UnitRequest(id, "kg", kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void updateWithoutId() {
        final String kg = "2kg";
        final String kilogram = "kilogram";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS + "/",
                HttpMethod.PUT,
                login(new UnitRequest(null, kg, kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody().message());
    }

    @Test
    void updateWithDifferentId() {
        final String id = "1";
        final String kg = "2kg";
        final String kilogram = "kilogram";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.PUT,
                login(new UnitRequest("2", kg, kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWithoutShortname() {
        final String id = "1";
        final String kilogram = "kilogram";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                UNITS + "/" + id,
                HttpMethod.PUT,
                login(new UnitRequest(id, null, kilogram)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    private HttpEntity login() {
        return login(null);
    }

    private HttpEntity login(final UnitRequest unitRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic %s".formatted(Base64.getEncoder().encodeToString("username:password".getBytes(StandardCharsets.UTF_8))));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.exchange(
                LOGIN,
                HttpMethod.POST,
                entity,
                LoginResponse.class
        );

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(loginResponse.getBody().token()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(unitRequest, headers);
    }

}