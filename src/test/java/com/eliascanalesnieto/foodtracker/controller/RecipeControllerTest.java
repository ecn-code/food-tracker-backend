package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.config.MockConfig;
import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.dto.out.ProductValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.RecipeResponse;
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
class RecipeControllerTest {

    private static final String LOGIN = "/users/login";
    private static final String RECIPES = "/recipes";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppConfig appConfig;

    @Test
    void getAll() {
        ResponseEntity<List<RecipeResponse>> response = testRestTemplate.exchange(
                RECIPES,
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
                                getRecipe1(),
                                getRecipe2()
                        )
                );
    }

    @Test
    void getOne() {
        final String id = "1";
        ResponseEntity<RecipeResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.GET,
                login(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(getRecipe1());
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
        ResponseEntity<RecipeResponse> response = testRestTemplate.exchange(
                RECIPES,
                HttpMethod.POST,
                login(getRecipeRequest(null, "name")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new RecipeResponse(null, "name", "Receta tradicional española",
                        List.of(new ProductValueResponse("1", "Leche", "Leche entera de vaca", null, "g", 500d),
                                new ProductValueResponse("2", "Pan", "Pan integral", "1", "portions", 4d)),
                        List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 1300d))));

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
                login(getRecipeRequest("55", "name")),
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
                login(getRecipeRequest(null, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void update() {
        final String id = "1";
        final String name = "Tortilla de patatas";

        ResponseEntity<RecipeResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.PUT,
                login(getRecipeRequest(id, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(new RecipeResponse(id, name + "-m", "Receta tradicional española",
                        List.of(new ProductValueResponse("1", "Leche", "Leche entera de vaca", null, "g", 500d),
                                new ProductValueResponse("2", "Pan", "Pan integral", "1", "portions", 4d)),
                        List.of(new NutritionalValueResponse("1", "Kilocaloría", "kcal", "k", 1300d))));

        testRestTemplate.exchange(
                RECIPES + "/" + id,
                HttpMethod.PUT,
                login(getRecipeRequest(id, name)),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    void updateWithoutId() {
        final String name = "recipe 1";

        ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(
                RECIPES + "/",
                HttpMethod.PUT,
                login(getRecipeRequest(null, name + "-m")),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertNull(response.getBody().message());
    }

    @Test
    void updateWithDifferentId() {
        final String id = "1";
        final String name = "recipe 1";

        ResponseEntity<RecipeResponse> response = testRestTemplate.exchange(
                RECIPES + "/" + id + "1",
                HttpMethod.PUT,
                login(getRecipeRequest(id, name + "-m")),
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
                login(getRecipeRequest(id, null)),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertNull(response.getBody());
    }

    private HttpEntity login() {
        return login(null);
    }

    private HttpEntity login(final RecipeRequest RecipeRequest) {
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

        return new HttpEntity<>(RecipeRequest, headers);
    }

    private static RecipeResponse getRecipe2() {
        return new RecipeResponse("2", "Tortilla de jamon", "Receta tortilla jamon",
                List.of(new ProductValueResponse("3", "Patata", null, null, "g", 500d),
                        new ProductValueResponse("4", "Huevo", null, null, "unidad", 4d),
                        new ProductValueResponse("5", "Jamon", null, "j", "g", 40d)),
                List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 800d)));
    }

    private static RecipeResponse getRecipe1() {
        return new RecipeResponse("1", "Tortilla de patatas", "Receta tradicional española",
                List.of(new ProductValueResponse("3", "Patata", null, null, "g", 500d),
                        new ProductValueResponse("4", "Huevo", null, null, "unidad", 4d)),
                List.of(new NutritionalValueResponse("1", "Calorías", "cal", "kcal", 800d)));
    }

    private static RecipeRequest getRecipeRequest(final String id, final String name) {
        return new RecipeRequest(id, name, "Receta tradicional española",
                List.of(new ProductValueRequest("1", 500d),
                        new ProductValueRequest("2", 4d)));
    }

}