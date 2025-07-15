package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.ProductValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.RecipeResponse;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.ProductValue;
import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
import com.eliascanalesnieto.foodtracker.model.Recipe;
import com.eliascanalesnieto.foodtracker.repository.ProductRepository;
import com.eliascanalesnieto.foodtracker.repository.RecipeRepository;
import com.eliascanalesnieto.foodtracker.utils.NutritionalValueCalculator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

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

        final RecipeDynamo recipeDynamo = recipeRepository.create(getRecipeWithNutritionalValues(recipeRequest));
        return toResponse(recipeDynamo);
    }

    public RecipeResponse put(final String id, final RecipeRequest recipeRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(recipeRequest.id()) || !recipeRequest.id().equals(id)) {
            throw new UnprocessableContent();
        }
        final RecipeDynamo recipeDynamo = recipeRepository.update(getRecipeWithNutritionalValues(recipeRequest));
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
                        .map(iv -> new ProductValueResponse(iv.getId(), iv.getName(), iv.getDescription(), iv.getRecipeId(), iv.getUnit(), iv.getValue()))
                        .collect(Collectors.toList())
                        : null,
                data.getNutritionalValues() != null
                        ? data.getNutritionalValues().stream()
                        .map(iv -> new NutritionalValueResponse(iv.getId(), iv.getName(), iv.getShortName(), iv.getUnit(), iv.getValue()))
                        .collect(Collectors.toList())
                        : null
        );
    }

    private Recipe getRecipeWithNutritionalValues(final RecipeRequest recipeRequest) {
        final List<ProductValue> products = recipeRequest.products().stream()
                .map(productValueRequest ->
                        ProductValue.buildFromProductValue(getProduct(productValueRequest), productValueRequest.value())
                ).toList();

        final Collection<NutritionalValue> nutritionalValues = NutritionalValueCalculator.mergeList(products);

        return Recipe.build(recipeRequest, products, nutritionalValues);
    }

    @SneakyThrows
    private ProductDynamo getProduct(final ProductValueRequest productValueRequest) {
        return productRepository.get(productValueRequest.id());
    }
}