package com.eliascanalesnieto.foodtracker.config;

import com.eliascanalesnieto.foodtracker.dto.in.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
public class AuthorizationToUserLoginConverter implements Converter<String, Optional<UserLogin>> {

    @Override
    public Optional<UserLogin> convert(final String source)  {
        if (StringUtils.hasText(source) && source.startsWith("Basic ")) {
            final String base64Credentials = source.substring("Basic ".length());
            final String credentials = base64ToText(base64Credentials);
            final String[] values = credentials.split(":", 2);

            if(values.length < 2) {
                log.error("Not well defined Basic header");
                return Optional.empty();
            }

            return Optional.of(new UserLogin(values[0], values[1]));
        }

        log.error("Not has Basic header");
        return Optional.empty();
    }

    private String base64ToText(final String base64) {
        try {
            return new String(Base64.getDecoder().decode(base64));
        } catch (Exception e) {
            return "";
        }
    }

}
