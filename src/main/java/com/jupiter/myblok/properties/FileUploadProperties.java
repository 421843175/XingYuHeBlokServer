package com.jupiter.myblok.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "files.upload")
public class FileUploadProperties {

    private String path;
    private List<String> allowTypes;
}
