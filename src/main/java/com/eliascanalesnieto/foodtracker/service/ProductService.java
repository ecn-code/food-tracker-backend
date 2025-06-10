package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ItemValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.ProductResponse;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> get() {
        return productRepository.get().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse get(final String id) throws EntityNotFoundException {
        final ProductDynamo productDynamo = productRepository.get(id);
        return toResponse(productDynamo);
    }

    public ProductResponse post(final ProductRequest productRequest) throws UnprocessableContent {
        if (StringUtils.hasText(productRequest.id())) {
            throw new UnprocessableContent();
        }
        final ProductDynamo productDynamo = productRepository.create(productRequest);
        return toResponse(productDynamo);
    }

    public ProductResponse put(final String id, final ProductRequest productRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(productRequest.id()) || !productRequest.id().equals(id)) {
            throw new UnprocessableContent();
        }
        final ProductDynamo productDynamo = productRepository.update(productRequest);
        return toResponse(productDynamo);
    }

    public void delete(final String id) {
        productRepository.delete(id);
    }

    private ProductResponse toResponse(final ProductDynamo productDynamo) {
        var data = productDynamo.getData();
        return new ProductResponse(
                productDynamo.getId(),
                data.getName(),
                data.getDescription(),
                data.getRecipeId(),
                data.getNutritionalValues() != null
                        ? data.getNutritionalValues().stream()
                            .map(iv -> new ItemValueResponse(iv.getId(), iv.getName(), iv.getUnit(), iv.getQuantity()))
                            .collect(Collectors.toList())
                        : null
        );
    }
}
