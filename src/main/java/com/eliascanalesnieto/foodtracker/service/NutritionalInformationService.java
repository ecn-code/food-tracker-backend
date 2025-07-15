package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.in.NutritionalInformationRequest;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalInformationResponse;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.repository.NutritionalInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionalInformationService {

    private final NutritionalInformationRepository nutritionalInformationRepository;

    public List<NutritionalInformationResponse> get() {
        return nutritionalInformationRepository.get().stream()
                .map(ni -> new NutritionalInformationResponse(
                        ni.getId(),
                        ni.getData().getShortName(),
                        ni.getData().getName(),
                        ni.getData().getUnit())
                )
                .sorted()
                .toList();
    }

    public NutritionalInformationResponse get(final String id) throws EntityNotFoundException {
        final NutritionalInformationDynamo ni = nutritionalInformationRepository.get(id);
        return new NutritionalInformationResponse(
                ni.getId(),
                ni.getData().getShortName(),
                ni.getData().getName(),
                ni.getData().getUnit()
        );
    }

    public NutritionalInformationResponse post(final NutritionalInformationRequest request) throws UnprocessableContent {
        if (StringUtils.hasText(request.id())) {
            throw new UnprocessableContent();
        }

        final NutritionalInformationDynamo ni = nutritionalInformationRepository.create(request);

        return new NutritionalInformationResponse(
                ni.getId(),
                ni.getData().getShortName(),
                ni.getData().getName(),
                ni.getData().getUnit()
        );
    }

    public NutritionalInformationResponse put(final String id, final NutritionalInformationRequest request) throws UnprocessableContent {
        if (!StringUtils.hasText(request.id()) || !request.id().equals(id)) {
            throw new UnprocessableContent();
        }

        final NutritionalInformationDynamo ni = nutritionalInformationRepository.update(request);

        return new NutritionalInformationResponse(
                ni.getId(),
                ni.getData().getShortName(),
                ni.getData().getName(),
                ni.getData().getUnit()
        );
    }

    public void delete(final String id) {
        nutritionalInformationRepository.delete(id);
    }
}
