package com.stanydesa.blog.application.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Change the prefix of the Authorization token from Bearer to Token.
 */
@Component
public class CustomPrefixBearerTokenResolver implements BearerTokenResolver {
    // this custom token resolver is designed to handle bearer tokens in HTTP requests, specifically changing the expected prefix from "Bearer" to "Token"
    // and providing flexibility for extracting tokens from both the Authorization header and request parameters based on the request type.
    // It includes validation and error handling for malformed or multiple tokens.
    //Security risk to accept both Bearer and Token
    private static final Pattern AUTHORIZATION_PATTERN =
            //named capturing group named "mytoken"
            Pattern.compile("^Token (?<mytoken>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    @Override
    public String resolve(HttpServletRequest request) {
        //Responsible for extracting the token from the request
        String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
        String parameterToken =
                isParameterTokenSupportedForRequest(request) ? resolveFromRequestParameters(request) : null;

        if (authorizationHeaderToken != null) {
            if (parameterToken != null) {
                BearerTokenError error =
                        BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request (and also get parameters)");
                throw new OAuth2AuthenticationException(error);
            }
            return authorizationHeaderToken;
        }
        if(parameterToken!=null){
            return parameterToken;
        }
        return null;
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        //to extract the token from the Authorization header.
        //Usual route
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "token")) return null;
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
            throw new OAuth2AuthenticationException(error);
        }
        //extract from token
        return matcher.group("mytoken");
    }

    private boolean isParameterTokenSupportedForRequest(HttpServletRequest request) {
        //Checks whether parameter-based token extraction is supported based on the HTTP method and content type
        //Supports parameter extraction for POST requests with content type application/x-www-form-urlencoded and GET requests
        return (("POST".equals(request.getMethod())
                && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
                || "GET".equals(request.getMethod()));
    }

    private String resolveFromRequestParameters(HttpServletRequest request) {
        //Extracts the token from the "access_token" parameter in the request
        //For GET requests, the token is extracted from the "access_token" parameter in the request. If the parameter is not present or there are multiple tokens, an exception is thrown.
        //For POST requests with content type application/x-www-form-urlencoded, the token is also extracted from the "access_token" parameter in the request. If the parameter is not present or there are multiple tokens, an exception is thrown.
        String[] values = request.getParameterValues("access_token");
        if (values == null || values.length == 0) return null;
        if (values.length == 1) return values[0];
        //Handles cases where multiple tokens are present, throwing an exception in such cases
        BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
        throw new OAuth2AuthenticationException(error);
    }
}
