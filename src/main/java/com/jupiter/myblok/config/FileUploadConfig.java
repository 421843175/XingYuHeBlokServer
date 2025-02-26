package com.jupiter.myblok.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

@Configuration
public class FileUploadConfig {
    /**   单个数据大小 10M  */
    private static final String SINGLE_FILE_SIZE = "10240KB";
    /** 总上传数据大小 100M */
    private static final String TOTAL_FILE_SIZE = "10240KB";

    @Bean
    public MultipartConfigElement multipartConfigElement() throws IOException {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse(SINGLE_FILE_SIZE));
        ///
        factory.setMaxRequestSize(DataSize.parse(TOTAL_FILE_SIZE));
        return factory.createMultipartConfig();
    }
}