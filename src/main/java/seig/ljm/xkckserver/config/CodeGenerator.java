package seig.ljm.xkckserver.config;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:50001/xkck?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&remarks=true&useInformationSchema=true",
                        "user", "123456")
                .globalConfig(builder -> builder
                        .author("ljm")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .enableSwagger()
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("seig.ljm.xkckserver")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder().enableFileOverride() // 开启覆盖文件
                        .enableLombok().enableTableFieldAnnotation()
                        .enableTableFieldAnnotation() // 启用字段注解
                        .serviceBuilder().enableFileOverride().formatServiceFileName("%sService") // 格式化 service 名称
                        .controllerBuilder().enableFileOverride().enableRestStyle()
                        .mapperBuilder().enableFileOverride().enableBaseColumnList().enableBaseResultMap()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
