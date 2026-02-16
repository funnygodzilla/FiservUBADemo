package com.fiserv.uba.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.fiserv.com")
                                .description("Production Server")
                ))
                .info(new Info()
                        .title("Account Service API")
                        .version("1.0.0")
                        .description("REST API for managing bank accounts in the Fiserv UBA system")
                        .contact(new Contact()
                                .name("Fiserv Account Team")
                                .email("account-service@fiserv.com")
                                .url("https://fiserv.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
