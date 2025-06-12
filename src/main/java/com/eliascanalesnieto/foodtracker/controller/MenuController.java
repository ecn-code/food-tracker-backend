package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;
import com.eliascanalesnieto.foodtracker.dto.out.MenuResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public List<MenuResponse> get(@Auth final User currentUser) {
        return menuService.get();
    }

    @GetMapping("/{id}")
    public MenuResponse get(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException, ParseException {
        return menuService.get(id);
    }

    @PostMapping
    public MenuResponse post(@Auth final User currentUser, @RequestBody final MenuRequest menuRequest) throws UnprocessableContent, ParseException, EntityNotFoundException {
        validate(menuRequest);
        return menuService.post(menuRequest);
    }

    @PutMapping("/{id}")
    public MenuResponse put(@Auth final User currentUser, @PathVariable final String id, @RequestBody final MenuRequest menuRequest) throws UnprocessableContent, ParseException, EntityNotFoundException {
        validate(menuRequest);
        return menuService.put(id, menuRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException {
        menuService.delete(id);
    }

    private void validate(final MenuRequest menuRequest) throws UnprocessableContent {
        if (menuRequest.date() == null || !StringUtils.hasText(menuRequest.username())) {
            throw new UnprocessableContent();
        }
    }
}
