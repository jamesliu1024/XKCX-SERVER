package seig.ljm.xkckserver.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("seig.ljm.xkckserver.mapper")
public class MyBatisPlusConfig {
}
