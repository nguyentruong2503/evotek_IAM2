package com.example.iam2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Cấu hình bảo mật (nút Authorize cho JWT)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // Thông tin chung của API
                .info(new Info()
                        .title("IAM System API Documentation")
                        .description("""
                                Hệ thống quản lý người dùng, vai trò và phân quyền (RBAC).
                                - Hỗ trợ self-IDP và tích hợp Keycloak.
                                - Có chức năng Auditor, phân trang, logging và xóa mềm.
                                - Tích hợp JWT cho bảo mật.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IAM Team")
                                .email("team@example.com")
                                .url("https://github.com/your-repo"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
