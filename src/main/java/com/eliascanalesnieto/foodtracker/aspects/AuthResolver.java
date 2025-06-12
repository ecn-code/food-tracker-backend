package com.eliascanalesnieto.foodtracker.aspects;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.exception.AuthorizationException;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.AuthorizationService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    public static final String BEARER_ = "Bearer ";
    private final AuthorizationService authorizationService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws AuthorizationException {

        final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        final String authorization = request.getHeader("Authorization");

        if(!StringUtils.hasText(authorization) || !authorization.contains(BEARER_)) {
            throw new AuthorizationException();
        }

        final String token = authorization.replace(BEARER_, "");
        final Claims claims = authorizationService.validate(token);

        return new User(claims.getSubject());
    }

}
