package io.github.geniot.sayagain;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@Getter
public class WebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(WebConfig.class);

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**");
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addRedirectViewController("/", "index.html");
//    }
//
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**/*.*").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

//    @Bean
//    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
//    }
//
//    // Second Method: Using RestTemplateBuilder
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder.build();
//    }
//
//    @Bean
//    public InternalResourceViewResolver defaultViewResolver() {
//        return new InternalResourceViewResolver();
//    }

    @Bean
    SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    @Bean
    public SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

}

