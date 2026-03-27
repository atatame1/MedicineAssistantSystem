package com.atatame.medicineassistantsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("MedicineAssistantSystem")
                        .version("1.0")
                        .description("MAS接口文档")
                        .contact(new Contact().name("atatame")));
    }


    @Bean
    public GroupedOpenApi loginApi(){
        return GroupedOpenApi.builder().group("api").pathsToMatch("/**").build();
    }
}
