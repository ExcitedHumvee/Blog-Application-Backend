package com.stanydesa.blog.application.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    //comprehensive security policy for app
    //authentication, authorization, exception handling strategies
    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${security.key.public}") RSAPublicKey rsaPublicKey,
            @Value("${security.key.private}") RSAPrivateKey rsaPrivateKey) {
        //Asymmetric encryption
        //Nimbus JOSE + JWT library to build an RSA key pair
        JWK jwk = new RSAKey.Builder(rsaPublicKey).privateKey(rsaPrivateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.key.public}") RSAPublicKey rsaPublicKey) {
        //only public key recquired to decode
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ExceptionHandleFilter exceptionHandleFilter)
            throws Exception {
        return http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)//disables HTTP basic authentication, Cross-Site Request Forgery
                //csrf means attacker will create a new url and through social engineering will get user to click on it
                //user has session cookies so url will work
                //csrf protection: server sends new token every session to html form not cookies
                .formLogin(AbstractHttpConfigurer::disable)// we are handling login ourselves using OAuth2.0, JWT
                .cors(SecurityConfigurerAdapter::and)//Cross-Origin Resource Sharing
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(HttpMethod.POST,
                                        "/api/users",
                                        "/api/users/login")
                                .permitAll()//unrestricted access
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/articles/{slug}/comments",
                                        "/api/articles/{slug}",
                                        "/api/articles",
                                        "/api/profiles/{username}",
                                        "/api/tags")
                                .permitAll()//unrestricted access

                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)//configures spring application as OAuth2 resource server using JWT for authentication
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))//application won't create or use HTTP sessions
                .exceptionHandling(
                        handler -> handler.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))//exception handling for authentication errors and access denied errors.
                .addFilterBefore(exceptionHandleFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //new
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        //configures CORS for the Spring application by allowing requests from any origin, any method, and with any headers. Additionally, it specifies that credentials should be included in cross-origin requests. The configuration is applied to all paths in the application (/**)
        CorsConfiguration cors = new CorsConfiguration();

        cors.setAllowedOriginPatterns(List.of("*"));//Allows requests from any origin
        cors.setAllowedMethods(List.of("*"));//Allows any HTTP method GET, POST, PUT, DELETE, etc.
        cors.setAllowedHeaders(List.of("*"));//Allows any HTTP headers in the request
        cors.setAllowCredentials(true);//Specifies that the browser should include credentials (e.g., cookies, HTTP authentication) when making the actual request

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);//the CORS configuration applies to all paths in the application

        return source;
    }
}
