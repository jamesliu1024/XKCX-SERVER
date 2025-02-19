package seig.ljm.xkckserver.common.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomFastJsonConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        
        // 配置 FastJson
        com.alibaba.fastjson.support.config.FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
            SerializerFeature.WriteMapNullValue,        // 输出空值字段
            SerializerFeature.WriteDateUseDateFormat,   // 日期格式化
            SerializerFeature.WriteNullListAsEmpty,     // List字段如果为null,输出为[]
            SerializerFeature.WriteNullStringAsEmpty,   // 字符类型字段如果为null,输出为""
            SerializerFeature.DisableCircularReferenceDetect // 禁用循环引用检测
        );
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");   // 设置日期格式
        config.setCharset(StandardCharsets.UTF_8);
        
        // 设置支持的媒体类型
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        
        converter.setSupportedMediaTypes(supportedMediaTypes);
        converter.setFastJsonConfig(config);
        
        converters.add(0, converter); // 添加到首位
    }
}