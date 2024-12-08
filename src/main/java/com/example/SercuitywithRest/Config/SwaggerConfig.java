package com.example.SercuitywithRest.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    // OpenAPI 3 configuration
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0")
                        .description("API documentation for the User Management system"));
    }

    // Swagger 2 Docket Configuration
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.SercuitywithRest"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(securityScheme()))
                .securityContexts(Arrays.asList(securityContext()))
                .apiInfo(new ApiInfoBuilder()
                        .title("User Management API")
                        .version("1.0")
                        .description("API documentation for the User Management system")
                        .build());
    }

    // Security scheme for Basic Authentication
    private SecurityScheme securityScheme() {
        return new BasicAuth("basicAuth");
    }

    // Security context to apply to all paths
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    // Default authentication setup
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("basicAuth", authorizationScopes));
    }
}
