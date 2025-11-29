package com.itmk.config.SwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // 使用 OpenAPI 3.0
                .apiInfo(apiInfo())
                .select()
                // 指定扫描的包路径，这里我们指向你的 Controller
                .apis(RequestHandlerSelectors.basePackage("com.itmk.netSystem"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("医院后台管理系统 API 文档")
                .description("提供排班、医生、科室等模块的接口")
                .contact(new Contact("你的名字", "http://yourwebsite.com", "your-email@example.com"))
                .version("1.0")
                .build();
    }
}