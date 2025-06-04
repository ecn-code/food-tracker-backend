package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.config.AuthorizationToUserLoginConverter;
import com.eliascanalesnieto.foodtracker.dto.in.UserLogin;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.dto.out.UserResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.LoginException;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.AuthorizationService;
import com.eliascanalesnieto.foodtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthorizationToUserLoginConverter authorizationToUserLoginConverter;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestHeader("Authorization") String authHeader) throws LoginException {
        UserLogin userLogin = authorizationToUserLoginConverter.convert(authHeader).orElseThrow(LoginException::new);
        return authorizationService.createToken(userLogin);
    }

    @GetMapping("/{username}")
    public UserResponse get(@Auth final User currentUser, final @PathVariable String username) throws EntityNotFoundException {
        log.debug("Current user {}", currentUser);
        return new UserResponse(userService.get(username).username());
    }

}
