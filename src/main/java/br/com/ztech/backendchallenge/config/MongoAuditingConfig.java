package br.com.ztech.backendchallenge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
@Profile({"dev", "ti", "qa", "prod", "local", "docker", "integration"})
public class MongoAuditingConfig {
}
