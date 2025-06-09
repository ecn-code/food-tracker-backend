package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ProductResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> get(@Auth final User currentUser) {
        return productService.get();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException {
        return productService.get(id);
    }

    @PostMapping
    public ProductResponse post(@Auth final User currentUser, @RequestBody final ProductRequest productRequest) throws UnprocessableContent {
        validate(productRequest);
        return productService.post(productRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse put(@Auth final User currentUser, @PathVariable final String id, @RequestBody final ProductRequest productRequest) throws UnprocessableContent {
        validate(productRequest);
        return productService.put(id, productRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@Auth final User currentUser, @PathVariable final String id) {
        productService.delete(id);
    }

    private void validate(final ProductRequest productRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(productRequest.name())) {
            throw new UnprocessableContent();
        }
    }
}
