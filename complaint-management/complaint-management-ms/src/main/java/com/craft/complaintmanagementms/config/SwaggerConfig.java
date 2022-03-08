package com.craft.complaintmanagementms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String appName;

    final static private String[][] GROUP_VS_BASE_NAME = {
            {"All-API", "NA"},
            {"MS-API", SwaggerConfig.class.getPackage().getName()}
    };

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(appName)
                .description(appName + " API Documentation")
                .version("1.0.0")
                .build();
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .enable(true)
                .select()
                .paths(PathSelectors.any())
                .build();
    }
}
