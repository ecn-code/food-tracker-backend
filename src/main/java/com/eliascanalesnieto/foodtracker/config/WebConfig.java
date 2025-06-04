package com.eliascanalesnieto.foodtracker.config;

import com.eliascanalesnieto.foodtracker.aspects.AuthResolver;
import com.eliascanalesnieto.foodtracker.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthorizationService authorizationService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthResolver(authorizationService));
    }
}