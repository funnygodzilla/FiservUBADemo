package com.fiserv.uba.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fiserv UBA Account Service API")
                        .description("API documentation for account-service")
                        .version("1.0.0")
                        .contact(new Contact().name("Fiserv UBA Team").email("support@fiserv.com"))
                        .license(new License().name("Proprietary").url("https://fiserv.com")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8081").description("Local")
                ));
    }
}
