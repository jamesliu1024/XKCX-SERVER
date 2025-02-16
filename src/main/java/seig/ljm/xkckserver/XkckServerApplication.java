package seig.ljm.xkckserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("seig.ljm.xkckserver.mapper")
public class XkckServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XkckServerApplication.class, args);
    }

}
