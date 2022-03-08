package com.craft.complaintmanagementms.config.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MongoDB development (not secured) configuration.
 */
@Configuration
public class MongoConfiguration extends BaseMongoConfiguration {

    /**
     * Value defined in dev properties db name
     */
    @Value("${mongodb.database}")
    private String database;

    /**
     * Value defined in dev properties for db port
     */
    @Value("${mongodb.port}")
    private String port;

    /**
     * Value defined in dev properties for user
     */
    @Value("${mongodb.user}")
    private String user;

    /**
     * Value defined in dev properties for password
     */
    @Value("${mongodb.password}")
    private String password;

    /**
     * Value defined in dev properties for host
     */
    @Value("${mongodb.host}")
    private String host;

    /**
     * for local run without credentials
     */
    @Value("${mongodb.credentials.enable:true}")
    private Boolean credentialsEnable;

    @Bean
    @Override
    public MongoClient mongoClient() {

        String connectionString;
        if (credentialsEnable) {
            connectionString = String.format("mongodb://%s:%s@%s:%s/%s", user, password, host, port, database);
        } else {
            connectionString = String.format("mongodb://%s:%s/%s", host, port, database);
        }

        return MongoClients.create(getMongoClientSettings(connectionString));
    }

    private MongoClientSettings getMongoClientSettings(String connectionString) {
        return MongoClientSettings.builder().
                uuidRepresentation(UuidRepresentation.JAVA_LEGACY).
                applyConnectionString(new ConnectionString(connectionString)).build();
    }

}
