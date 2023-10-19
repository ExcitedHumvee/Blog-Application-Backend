package com.stanydesa.blog.application.config;

import com.stanydesa.blog.domain.user.User;
import com.stanydesa.blog.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    //Without this code and WebMvcConfiguration, we are not able to read the authorization token from the get current user route
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //Overrides a method to indicate whether the resolver supports the given method parameter. In this case, it returns true if the parameter type is User.
        return parameter.getParameterType() == User.class;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        // Overrides a method to resolve the method argument.
        // It checks the user authentication status, and if the user is anonymous, it returns an anonymous user.
        // Otherwise, it retrieves the user ID and token from the JwtAuthenticationToken and uses the UserRepository to find the corresponding user by ID.
        // If found, it sets the token on the user instance; otherwise, it throws an InvalidBearerTokenException.

        if (authentication instanceof AnonymousAuthenticationToken) {
            return User.anonymous();
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String userId = jwtAuthenticationToken.getName().strip();
        String token = jwtAuthenticationToken.getToken().getTokenValue().strip();

        return userRepository
                .findById(UUID.fromString(userId))
                .map(it -> it.setToken(token))
                .orElseThrow(() -> new InvalidBearerTokenException("`%s` is invalid token".formatted(token)));
    }
}
