package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.config.MockConfig;
import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;
import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.dto.out.MenuResponse;
import com.eliascanalesnieto.foodtracker.dto.out.ProductValueResponse;
import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import lombok.SneakyThrows;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockConfig.class)
class MenuControllerTest {

    private static final String LOGIN = "/users/login";
    private static final String MENUS = "/menus";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppConfig appConfig;

    @Test
    void getAll() {
        ResponseEntity<List<MenuResponse>> response = testRestTemplate.exchange(
                MENUS,
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
                                getMenu1(),
                                getMenu2()
                        )
                );
    }

    @Test
    void getOne() {
        final MenuResponse menuResponse = getMenu1();
        ResponseEntity<MenuResponse> response = testRestTemplate.exchange(
                MENUS + "/" + IdFormat.format(menuResponse.date(), menuResponse.username()),
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(menuResponse);
    }

    @Test
    void getOneDoesNotExist() {
        final String id = "8";
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                MENUS + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @SneakyThrows
    void create() {
        ResponseEntity<MenuResponse> response = testRestTemplate.exchange(
                MENUS,
                HttpMethod.POST,
                login(getMenuRequest(DateFormat.parse("2025-10-10"), "name")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(new MenuResponse("2025-10-10", "name",
                        Map.of("Almuerzo", List.of(new ProductValueResponse("1", "p1", null, "portions", 5d))),
                        List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 300d))));

        final ResponseEntity<Object> delete = testRestTemplate.exchange(
                MENUS + "/" + IdFormat.format(response.getBody().date(), response.getBody().username()),
                HttpMethod.DELETE,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatusCode.valueOf(200), delete.getStatusCode());
    }

    @Test
    void createWithoutName() {
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                MENUS,
                HttpMethod.POST,
                login(getMenuRequest(null, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    @SneakyThrows
    void update() {
        final String yyyyMMdd = "2025-01-12";
        final Date date = DateFormat.parse(yyyyMMdd);
        final String username = "user3";

        ResponseEntity<MenuResponse> toUpdate = testRestTemplate.exchange(
                MENUS + "/" + IdFormat.format(yyyyMMdd, username),
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );
        MenuResponse menu = toUpdate.getBody();

        ResponseEntity<MenuResponse> response = testRestTemplate.exchange(
                MENUS + "/" + IdFormat.format(yyyyMMdd, username),
                HttpMethod.PUT,
                login(new MenuRequest(date, menu.username(), menu.products().entrySet()
                        .stream().collect(Collectors
                                .toMap(key -> "Cena",
                                        items -> items.getValue()
                                                .stream()
                                                .map(item ->
                                                        new ProductValueRequest(item.id(), item.name(), item.recipeId(), item.unit(), item.value())
                                                ).toList()
                                )
                        )
                )),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(new MenuResponse(yyyyMMdd, username, menu.products().entrySet()
                        .stream().collect(Collectors
                                .toMap(key -> "Cena",
                                        items -> items.getValue()
                                                .stream()
                                                .map(item ->
                                                        new ProductValueResponse(item.id(), item.name(), item.recipeId(), item.unit(), item.value())
                                                ).toList()
                                )
                        ), List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 310d))));

        testRestTemplate.exchange(
                MENUS + "/" + date + "#" + username,
                HttpMethod.PUT,
                login(new MenuRequest(date, menu.username(), menu.products().entrySet()
                        .stream().collect(Collectors
                                .toMap(key -> "Almuerzo",
                                        items -> items.getValue()
                                                .stream()
                                                .map(item ->
                                                        new ProductValueRequest(item.id(), item.name(), item.recipeId(), item.unit(), item.value())
                                                ).toList()
                                )
                        )
                )),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void updateWithoutId() {
        final String name = "menu 1";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                MENUS + "/",
                HttpMethod.PUT,
                login(getMenuRequest(null, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody().message());
    }

    @Test
    void updateWithDifferentId() {
        final Date id = new Date();
        final String name = "menu 1";

        ResponseEntity<MenuResponse> response = testRestTemplate.exchange(
                MENUS + "/" + id + "1",
                HttpMethod.PUT,
                login(getMenuRequest(id, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWithoutUsername() {
        final Date date = new Date();

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                MENUS + "/" + date,
                HttpMethod.PUT,
                login(getMenuRequest(date, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    private HttpEntity<MenuRequest> login() {
        return login(null);
    }

    private HttpEntity<MenuRequest> login(final MenuRequest MenuRequest) {
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

        return new HttpEntity<>(MenuRequest, headers);
    }

    @SneakyThrows
    private static MenuResponse getMenu2() {
        return new MenuResponse("2025-12-12", "user",
                Map.of("Cena", List.of(new ProductValueResponse("1", "Patata", null, "g", 500d),
                        new ProductValueResponse("2", "Huevo", null, "unidad", 4d),
                        new ProductValueResponse("3", "Jamon", "1", "portion", 40d))),
                List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 800d)));
    }

    @SneakyThrows
    private static MenuResponse getMenu1() {
        return new MenuResponse("2025-01-12", "user3",
                Map.of("Almuerzo", List.of(new ProductValueResponse("1", "Patata", null, "g", 500d),
                        new ProductValueResponse("2", "Huevo", null, "unidad", 4d))),
                List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 800d)));
    }

    private static MenuRequest getMenuRequest(final Date date, final String username) {
        final Map<String, List<ProductValueRequest>> products = Map.of("Almuerzo",
                List.of(new ProductValueRequest("1", "p1", "1", "portions", 5d)));

        return new MenuRequest(date, username, products);
    }

}