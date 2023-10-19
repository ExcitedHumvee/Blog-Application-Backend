package com.stanydesa.blog.application.config;

import com.stanydesa.blog.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
    //This class is a Spring configuration class that implements WebMvcConfigurer, allowing for customization of the Spring MVC configuration
    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //Overrides a method to add custom argument resolvers. In this case, it adds an instance of UserArgumentResolver to the list of resolvers
        resolvers.add(new UserArgumentResolver(userRepository));
    }
}
