package ru.mathleague.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("main");
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/styles/css/unauth/**")
                .addResourceLocations("classpath:/static/css/unauth/");

        registry
                .addResourceHandler("/styles/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry
                .addResourceHandler("/styles/font/**")
                .addResourceLocations("classpath:/static/font/");

        registry
                .addResourceHandler("/js/unauth/**")
                .addResourceLocations("classpath:/static/js/unauth/");

        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("classpath:/images/");

        registry
                .addResourceHandler("/images/unauth/**")
                .addResourceLocations("classpath:/images/unauth/");
    }

}
