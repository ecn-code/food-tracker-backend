package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.config.MockConfig;
import com.eliascanalesnieto.foodtracker.dto.in.NutritionalValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.PaginatedList;
import com.eliascanalesnieto.foodtracker.dto.out.ProductResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockConfig.class)
class ProductControllerTest {

    private static final String LOGIN = "/users/login";
    private static final String RECIPES = "/products";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppConfig appConfig;

    @Test
    void getAll() {
        ResponseEntity<PaginatedList<ProductResponse>> response = testRestTemplate.exchange(
                RECIPES + "?items_per_page={items_per_page}",
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {},
                Map.of("items_per_page", 20)
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(
                        new PaginatedList<>(
                                List.of(
                                        getProduct4(),
                                        getProduct5(),
                                        getProduct1(),
                                        getProduct2(),
                                        getProduct3()
                                ),
                                null
                        )
                );
    }

    @Test
    void getOne() {
        final String id = "1";
        ResponseEntity<ProductResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(getProduct1());
    }

    @Test
    void getOneDoesNotExist() {
        final String id = "8";
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
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
        ResponseEntity<ProductResponse> response = testRestTemplate.exchange(
                RECIPES,
                HttpMethod.POST,
                login(getProductRequest(null, "name")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new ProductResponse(null, "name", "desc", "",
                        List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 3d))));

        testRestTemplate.exchange(
                RECIPES + "/" + response.getBody().id(),
                HttpMethod.DELETE,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void createWithId() {
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES,
                HttpMethod.POST,
                login(getProductRequest("55", "name")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createWithoutName() {
        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES,
                HttpMethod.POST,
                login(getProductRequest(null, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void update() {
        final String id = "1";

        ResponseEntity<ProductResponse> toUpdate = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );
        ProductResponse product = toUpdate.getBody();

        ResponseEntity<ProductResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.PUT,
                login(new ProductRequest(product.id(), product.name() + "-m", product.description(), product.recipeId(),
                        product.nutritionalValues().stream()
                                .map(i -> new NutritionalValueRequest(i.id(), i.value()))
                                .toList())),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(new ProductResponse(id, product.name() + "-m", product.description(), product.recipeId(), product.nutritionalValues()));

        testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.PUT,
                login(new ProductRequest(product.id(), product.name(), product.description(), product.recipeId(),
                        product.nutritionalValues().stream()
                                .map(i -> new NutritionalValueRequest(i.id(), i.value()))
                                .toList())),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void updateWithoutId() {
        final String name = "product 1";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES + "/",
                HttpMethod.PUT,
                login(getProductRequest(null, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody().message());
    }

    @Test
    void updateWithDifferentId() {
        final String id = "1";
        final String name = "product 1";

        ResponseEntity<ProductResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id + "1",
                HttpMethod.PUT,
                login(getProductRequest(id, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWithoutName() {
        final String id = "1";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.PUT,
                login(getProductRequest(id, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    private HttpEntity login() {
        return login(null);
    }

    private HttpEntity login(final ProductRequest ProductRequest) {
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

        return new HttpEntity<>(ProductRequest, headers);
    }

    private static ProductResponse getProduct2() {
        return new ProductResponse("2", "Pan", "Pan integral", "1",
                List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 250d)));
    }

    private static ProductResponse getProduct1() {
        return new ProductResponse("1", "Leche", "Leche entera de vaca", null,
                List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 60d)));
    }

    private static ProductResponse getProduct3() {
        return new ProductResponse("3", "Patata", "Patata integral", null,
                List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 50d)));
    }

    private static ProductResponse getProduct4() {
        return new ProductResponse("4", "Huevo", null, "2",
                List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 20d)));
    }

    private static ProductResponse getProduct5() {
        return new ProductResponse("5", "Jamon", null, "1",
                List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 750d)));
    }

    private static ProductRequest getProductRequest(final String id, final String name) {
        final String description = "desc";
        final List<NutritionalValueRequest> nutritionalValues = List.of(new NutritionalValueRequest("1", 3d));

        return new ProductRequest(id, name, description, "", nutritionalValues);
    }

}