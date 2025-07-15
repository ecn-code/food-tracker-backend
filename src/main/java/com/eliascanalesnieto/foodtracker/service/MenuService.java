package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;
import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;
import com.eliascanalesnieto.foodtracker.dto.out.MenuResponse;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.ProductValueResponse;
import com.eliascanalesnieto.foodtracker.entity.MenuDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.ProductValue;
import com.eliascanalesnieto.foodtracker.model.Menu;
import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
import com.eliascanalesnieto.foodtracker.repository.MenuRepository;
import com.eliascanalesnieto.foodtracker.repository.ProductRepository;
import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import com.eliascanalesnieto.foodtracker.utils.NutritionalValueCalculator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ProductRepository productRepository;

    @SneakyThrows
    public List<MenuResponse> get() {
        return menuRepository.get().stream()
                .map(this::toResponse)
                .toList();
    }

    public MenuResponse get(final String id) throws EntityNotFoundException {
        final MenuDynamo menuDynamo = menuRepository.get(id);
        return toResponse(menuDynamo);
    }

    public MenuResponse post(final MenuRequest menuRequest) throws UnprocessableContent, ParseException {
        if (menuRequest.date() == null || menuRequest.username() == null) {
            throw new UnprocessableContent();
        }
        final MenuDynamo menuDynamo = menuRepository.create(getMenuWithNutritionalValues(menuRequest));
        return toResponse(menuDynamo);
    }

    public MenuResponse put(final String id, final MenuRequest menuRequest) throws UnprocessableContent, ParseException {
        if (menuRequest.date() == null || menuRequest.username() == null) {
            throw new UnprocessableContent();
        }
        final MenuDynamo menuDynamo = menuRepository.update(id, getMenuWithNutritionalValues(menuRequest));
        return toResponse(menuDynamo);
    }

    public void delete(final String id) throws EntityNotFoundException {
        menuRepository.delete(id);
    }

    @SneakyThrows
    private MenuResponse toResponse(final MenuDynamo menuDynamo) {
        var data = menuDynamo.getData();
        return new MenuResponse(
                DateFormat.format(menuDynamo.getDate()),
                menuDynamo.getUsername(),
                data.getProducts() != null
                        ? data.getProducts().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue() != null
                                        ? e.getValue().stream()
                                        .map(iv -> new ProductValueResponse(iv.getId(), iv.getName(),
                                                iv.getDescription(), iv.getRecipeId(), iv.getUnit(), iv.getValue()))
                                        .collect(Collectors.toList())
                                        : null
                        ))
                        : null,
                data.getNutritionalValues() != null
                        ? data.getNutritionalValues().stream()
                        .map(iv -> new NutritionalValueResponse(iv.getId(), iv.getName(), iv.getShortName(), iv.getUnit(), iv.getValue()))
                        .collect(Collectors.toList())
                        : null
        );
    }

    private Menu getMenuWithNutritionalValues(final MenuRequest menuRequest) {
        final Map<String, List<ProductValue>> menus = menuRequest.products().entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), getProducts(entry.getValue())))
                .collect(
                        Collectors.toMap(
                                AbstractMap.SimpleEntry::getKey,
                                AbstractMap.SimpleEntry::getValue
                        )
                );

        final Collection<NutritionalValue> nutritionalValues = NutritionalValueCalculator.mergeListOfLists(menus.values());

        return Menu.build(menuRequest, menus, nutritionalValues);
    }

    private List<ProductValue> getProducts(final List<ProductValueRequest> productValueRequests) {
        return productValueRequests.stream()
                .map(productValueRequest -> ProductValue.buildFromProductValue(
                        getProduct(productValueRequest),
                        productValueRequest.value()
                )).toList();
    }

    @SneakyThrows
    private ProductDynamo getProduct(final ProductValueRequest productValueRequest) {
        return productRepository.get(productValueRequest.id());
    }
}
