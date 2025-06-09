package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ItemValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.RecipeResponse;
import com.eliascanalesnieto.foodtracker.entity.RecipeDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<RecipeResponse> get() {
        return recipeRepository.get().stream()
                .map(this::toResponse)
                .toList();
    }

    public RecipeResponse get(final String id) throws EntityNotFoundException {
        final RecipeDynamo recipeDynamo = recipeRepository.get(id);
        return toResponse(recipeDynamo);
    }

    public RecipeResponse post(final RecipeRequest recipeRequest) throws UnprocessableContent {
        if (StringUtils.hasText(recipeRequest.id())) {
            throw new UnprocessableContent();
        }
        final RecipeDynamo recipeDynamo = recipeRepository.create(recipeRequest);
        return toResponse(recipeDynamo);
    }

    public RecipeResponse put(final String id, final RecipeRequest recipeRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(recipeRequest.id()) || !recipeRequest.id().equals(id)) {
            throw new UnprocessableContent();
        }
        final RecipeDynamo recipeDynamo = recipeRepository.update(recipeRequest);
        return toResponse(recipeDynamo);
    }

    public void delete(final String id) {
        recipeRepository.delete(id);
    }

    private RecipeResponse toResponse(RecipeDynamo recipeDynamo) {
        var data = recipeDynamo.getData();
        return new RecipeResponse(
                recipeDynamo.getId(),
                data.getName(),
                data.getDescription(),
                data.getProducts() != null
                        ? data.getProducts().stream()
                            .map(iv -> new ItemValueResponse(iv.getName(), iv.getUnit(), iv.getQuantity()))
                            .collect(Collectors.toList())
                        : null,
                data.getNutritionalValues() != null
                        ? data.getNutritionalValues().stream()
                            .map(iv -> new ItemValueResponse(iv.getName(), iv.getUnit(), iv.getQuantity()))
                            .collect(Collectors.toList())
                        : null
        );
    }
}
