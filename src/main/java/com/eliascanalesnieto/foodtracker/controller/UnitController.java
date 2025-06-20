package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.in.UnitRequest;
import com.eliascanalesnieto.foodtracker.dto.out.PaginatedList;
import com.eliascanalesnieto.foodtracker.dto.out.UnitResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public PaginatedList<UnitResponse> get(@Auth final User currentUser) {
        return new PaginatedList<>(unitService.get(), null);
    }

    @GetMapping("/{id}")
    public UnitResponse get(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException {
        return unitService.get(id);
    }

    @PostMapping
    public UnitResponse post(@Auth final User currentUser, @RequestBody final UnitRequest unitRequest) throws UnprocessableContent {
        validate(unitRequest);
        return unitService.post(unitRequest);
    }

    @PutMapping("/{id}")
    public UnitResponse put(@Auth final User currentUser, @PathVariable final String id, @RequestBody final UnitRequest unitRequest) throws UnprocessableContent {
        validate(unitRequest);
        return unitService.put(id, unitRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@Auth final User currentUser, @PathVariable final String id) {
        unitService.delete(id);
    }

    private void validate(final UnitRequest unitRequest) throws UnprocessableContent {
        if(!StringUtils.hasText(unitRequest.name()) || !StringUtils.hasText(unitRequest.shortName())) {
            throw new UnprocessableContent();
        }
    }

}
