package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.dto.out.RecipeResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public List<RecipeResponse> get(@Auth final User currentUser) {
        return recipeService.get();
    }

    @GetMapping("/{id}")
    public RecipeResponse get(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException {
        return recipeService.get(id);
    }

    @PostMapping
    public RecipeResponse post(@Auth final User currentUser, @RequestBody final RecipeRequest recipeRequest) throws UnprocessableContent, EntityNotFoundException {
        validate(recipeRequest);
        return recipeService.post(recipeRequest);
    }

    @PutMapping("/{id}")
    public RecipeResponse put(@Auth final User currentUser, @PathVariable final String id, @RequestBody final RecipeRequest recipeRequest) throws UnprocessableContent, EntityNotFoundException {
        validate(recipeRequest);
        return recipeService.put(id, recipeRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@Auth final User currentUser, @PathVariable final String id) {
        recipeService.delete(id);
    }

    private void validate(final RecipeRequest recipeRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(recipeRequest.name())) {
            throw new UnprocessableContent();
        }
    }
}
