package com.soongsil.CoffeeChat.global.config.docs;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import lombok.Setter;

@Configuration
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("COGO API 문서").description("이상 있으면 말씀 부탁드립니다.");

        SecurityScheme bearerAuth =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("JWT");

        List<Server> servers =
                swaggerProperties.getServers().stream()
                        .map(s -> new Server().url(s.getUrl()).description(s.getDescription()))
                        .toList();

        return new OpenAPI()
                .servers(servers)
                .components(new Components().addSecuritySchemes("JWT", bearerAuth))
                .addSecurityItem(addSecurityItem)
                .info(info);
    }

    @Configuration
    @ConfigurationProperties(prefix = "swagger")
    @Getter
    @Setter
    public static class SwaggerProperties {
        private List<ServerEntry> servers;

        @Getter
        @Setter
        public static class ServerEntry {
            private String url;
            private String description;
        }
    }
}
