package com.clausetrail.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.net.InetSocketAddress;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile);
        String maskedUri = mongoUri != null ? mongoUri.replaceAll("://([^:]+):([^@]+)@", "://$1:****@") : "not set";

        try {
            log.info("Connecting to MongoDB (profile: {}) at: {}", activeProfile, maskedUri);
            MongoClient atlasClient = MongoClients.create(mongoUri);
            // Quick ping test
            atlasClient.getDatabase("admin").runCommand(new Document("ping", 1));
            log.info("Successfully connected to MongoDB cluster!");
            return atlasClient;
        } catch (Exception e) {
            if (isProd) {
                log.error("==========================================================================");
                log.error("FATAL: Failed to connect to MongoDB Atlas in production: {}", e.getMessage());
                log.error("Troubleshooting steps:");
                log.error("1. Go to MongoDB Atlas -> Network Access -> IP Access List.");
                log.error("2. Add '0.0.0.0/0' (Allow Access from Anywhere). Render uses dynamic cloud IPs.");
                log.error("3. Verify the MONGODB_URI environment variable in Render Dashboard -> Environment.");
                log.error("==========================================================================");
                throw new IllegalStateException("Production MongoDB connection failed: " + e.getMessage(), e);
            }

            log.warn("MongoDB connection failed ({}) in '{}' mode. Activating in-memory MongoDB engine for local dev...", e.getMessage(), activeProfile);
            try {
                MongoServer server = new MongoServer(new MemoryBackend());
                InetSocketAddress serverAddress = server.bind();
                String inMemoryUri = String.format("mongodb://%s:%d/clausetrail", serverAddress.getHostName(), serverAddress.getPort());
                log.info("In-memory MongoDB active on: {}", inMemoryUri);
                return MongoClients.create(inMemoryUri);
            } catch (Exception ex) {
                log.error("Failed to start in-memory MongoDB server", ex);
                throw new RuntimeException("Could not initialize MongoDB client", ex);
            }
        }
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, "clausetrail"));
    }
}
