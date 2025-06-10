package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;
import com.eliascanalesnieto.foodtracker.dto.out.ItemValueResponse;
import com.eliascanalesnieto.foodtracker.dto.out.MenuResponse;
import com.eliascanalesnieto.foodtracker.entity.MenuDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.repository.MenuRepository;
import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @SneakyThrows
    public List<MenuResponse> get() {
        return menuRepository.get().stream()
                .map(this::toResponse)
                .toList();
    }

    public MenuResponse get(final String id) throws EntityNotFoundException, ParseException {
        final MenuDynamo menuDynamo = menuRepository.get(id);
        return toResponse(menuDynamo);
    }

    public MenuResponse post(final MenuRequest menuRequest) throws UnprocessableContent, ParseException {
        if (menuRequest.date() == null || menuRequest.username() == null) {
            throw new UnprocessableContent();
        }
        final MenuDynamo menuDynamo = menuRepository.create(menuRequest);
        return toResponse(menuDynamo);
    }

    public MenuResponse put(final String id, final MenuRequest menuRequest) throws UnprocessableContent, ParseException {
        if (menuRequest.date() == null || menuRequest.username() == null) {
            throw new UnprocessableContent();
        }
        final MenuDynamo menuDynamo = menuRepository.update(id, menuRequest);
        return toResponse(menuDynamo);
    }

    public void delete(final String id) throws EntityNotFoundException {
        menuRepository.delete(id);
    }

    @SneakyThrows
    private MenuResponse toResponse(MenuDynamo menuDynamo) {
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
                                        .map(iv -> new ItemValueResponse(iv.getId(), iv.getName(), iv.getUnit(), iv.getQuantity()))
                                        .collect(Collectors.toList())
                                    : null
                            ))
                        : null,
                data.getNutritionalValues() != null
                        ? data.getNutritionalValues().stream()
                            .map(iv -> new ItemValueResponse(iv.getId(), iv.getName(), iv.getUnit(), iv.getQuantity()))
                            .collect(Collectors.toList())
                        : null
        );
    }
}
