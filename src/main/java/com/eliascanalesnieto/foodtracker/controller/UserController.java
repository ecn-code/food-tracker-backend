package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.config.AuthorizationToUserLoginConverter;
import com.eliascanalesnieto.foodtracker.dto.in.UserLogin;
import com.eliascanalesnieto.foodtracker.dto.out.LoginResponse;
import com.eliascanalesnieto.foodtracker.exception.LoginException;
import com.eliascanalesnieto.foodtracker.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthorizationToUserLoginConverter authorizationToUserLoginConverter;
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public LoginResponse login(@RequestHeader("Authorization") String authHeader) throws LoginException {
        UserLogin userLogin = authorizationToUserLoginConverter.convert(authHeader).orElseThrow(LoginException::new);
        return authorizationService.createToken(userLogin);
    }

}
