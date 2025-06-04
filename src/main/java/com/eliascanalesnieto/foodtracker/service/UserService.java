package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User get(final String username) throws EntityNotFoundException {
        return userRepository.get(username)
                .map(userDynamo -> new User(userDynamo.getUsername()))
                .orElseThrow(EntityNotFoundException::new);
    }

}
