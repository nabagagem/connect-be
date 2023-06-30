package com.nabagagem.connectbe.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("aws.s3")
public class S3Properties {
    private String filesBucket;
}
