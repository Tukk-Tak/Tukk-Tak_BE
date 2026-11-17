package com.ohgiraffer.tukk_tak.tukk_tak.global.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(@Value("${app.mongo.uri}") String uri) {
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build());
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient,
                                                     @Value("${app.mongo.database}") String database) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }
}
