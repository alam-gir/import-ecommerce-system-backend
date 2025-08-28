package com.importer_ecommerce.importEcommerce.cloudstorage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {
    
    private final CloudflareR2Config r2Config;
    
    @Bean
    public S3Client s3Client() {
        // For development, create a dummy client if properties are not properly configured
        if (r2Config.getEndpoint() == null || r2Config.getEndpoint().contains("dummy")) {
            // Return a dummy client for development
            return S3Client.builder()
                    .endpointOverride(URI.create("https://dummy.r2.cloudflarestorage.com"))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("dummy", "dummy")
                    ))
                    .region(Region.of("auto"))
                    .forcePathStyle(true)
                    .build();
        }
        
        return S3Client.builder()
                .endpointOverride(URI.create(r2Config.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                r2Config.getAccessKeyId(),
                                r2Config.getSecretAccessKey()
                        )
                ))
                .region(Region.of(r2Config.getRegion()))
                .forcePathStyle(true)
                .build();
    }
}
