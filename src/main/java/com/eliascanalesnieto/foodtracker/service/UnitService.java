package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.UnitRequest;
import com.eliascanalesnieto.foodtracker.dto.out.UnitResponse;
import com.eliascanalesnieto.foodtracker.entity.UnitDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public List<UnitResponse> get() {
        return unitRepository.get().stream()
                .map(unitDynamo -> new UnitResponse(
                        unitDynamo.getId(),
                        unitDynamo.getData().getShortName(),
                        unitDynamo.getData().getName())
                ).toList();
    }

    public UnitResponse get(final String id) throws EntityNotFoundException {
        final UnitDynamo unitDynamo = unitRepository.get(id);
        return new UnitResponse(
                unitDynamo.getId(),
                unitDynamo.getData().getShortName(),
                unitDynamo.getData().getName()
        );
    }

    public UnitResponse post(final UnitRequest unitRequest) throws UnprocessableContent {
        if (StringUtils.hasText(unitRequest.id())) {
            throw new UnprocessableContent();
        }

        final UnitDynamo unitDynamo = unitRepository.create(unitRequest);

        return new UnitResponse(unitDynamo.getId(),
                unitDynamo.getData().getShortName(),
                unitDynamo.getData().getName());
    }

    public UnitResponse put(final String id, final UnitRequest unitRequest) throws UnprocessableContent {
        if (!StringUtils.hasText(unitRequest.id()) || !unitRequest.id().equals(id)) {
            throw new UnprocessableContent();
        }

        final UnitDynamo unitDynamo = unitRepository.update(unitRequest);

        return new UnitResponse(unitDynamo.getId(),
                unitDynamo.getData().getShortName(),
                unitDynamo.getData().getName());
    }

    public void delete(final String id) {
        unitRepository.delete(id);
    }
}
