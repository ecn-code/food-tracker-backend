package com.eliascanalesnieto.config;

import com.eliascanalesnieto.dto.in.UserLogin;
import com.eliascanalesnieto.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Base64;
import java.util.Optional;

@Slf4j
public class AuthorizationToUserLoginConverter implements Converter<String, Optional<UserLogin>> {

    @Override
    public Optional<UserLogin> convert(String source)  {
        if (source.startsWith("Basic ")) {
            String base64Credentials = source.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);
            return Optional.of(new UserLogin(values[0], values[1]));
        }

        log.error("Error converting header to user login");
        return Optional.empty();
    }

}
