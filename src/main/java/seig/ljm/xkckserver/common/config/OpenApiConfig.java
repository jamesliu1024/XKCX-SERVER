package seig.ljm.xkckserver.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校口常开 API")
                        .description("校口常开后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Jarian")
                                .email("jamesliu1024@163.com")
                        ));
    }
}