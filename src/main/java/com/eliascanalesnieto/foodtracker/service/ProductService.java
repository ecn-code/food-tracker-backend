package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.NutritionalValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.PaginatedList;
import com.eliascanalesnieto.foodtracker.dto.out.ProductResponse;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
import com.eliascanalesnieto.foodtracker.model.Paginated;
import com.eliascanalesnieto.foodtracker.model.Product;
import com.eliascanalesnieto.foodtracker.repository.NutritionalInformationRepository;
import com.eliascanalesnieto.foodtracker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NutritionalInformationRepository nutritionalInformationRepository;

    public PaginatedList<ProductResponse> get(
            final Integer limit,
            final Map<String, AttributeValue> lastEvaluatedKey,
            final String query
    ) {
        final Paginated<ProductDynamo> productDynamoPaginatedList = productRepository.getSortByOrderBy(limit, lastEvaluatedKey, query);
        return new PaginatedList<>(
                productDynamoPaginatedList.items().stream().map(this::toResponse).toList(),
                PaginatedList.encodeLastEvaluatedKey(productDynamoPaginatedList.lastEvaluatedKey())
        );
    }

    public ProductResponse get(final String id) throws EntityNotFoundException {
        final ProductDynamo productDynamo = productRepository.get(id);
        return toResponse(productDynamo);
    }

    public ProductResponse post(final ProductRequest productRequest) throws UnprocessableContent {
        if (StringUtils.hasText(productRequest.id())) {
            throw new UnprocessableContent();
        }
        final ProductDynamo productDynamo = productRepository.create(
                build(productRequest)
        );
        return toResponse(productDynamo);
    }

    public ProductResponse put(final String id, final ProductRequest productRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(productRequest.id()) || !productRequest.id().equals(id)) {
            throw new UnprocessableContent();
        }
        final ProductDynamo productDynamo = productRepository.update(build(productRequest));
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
                        .map(iv -> new NutritionalValueResponse(iv.getId(), iv.getName(), iv.getShortName(), iv.getUnit(), iv.getValue()))
                        .sorted()
                        .collect(Collectors.toList())
                        : null
        );
    }

    @SneakyThrows
    private NutritionalInformationDynamo get(final NutritionalValueRequest nutritionalValueRequest) {
        return nutritionalInformationRepository.get(nutritionalValueRequest.id());
    }

    private Product build(ProductRequest productRequest) {
        return Product.build(productRequest, productRequest.nutritionalValues().stream()
                .map(nutritionalInformation ->
                        NutritionalValue.build(nutritionalInformation, get(nutritionalInformation))
                ).toList()
        );
    }
}
