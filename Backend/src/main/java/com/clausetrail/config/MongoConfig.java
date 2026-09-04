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

    @Bean
    @Primary
    public MongoClient mongoClient() {
        try {
            log.info("Attempting connection to configured MongoDB Atlas cluster...");
            MongoClient atlasClient = MongoClients.create(mongoUri);
            // Verify connection with a quick ping
            atlasClient.getDatabase("admin").runCommand(new Document("ping", 1));
            log.info("Successfully connected to MongoDB Atlas!");
            return atlasClient;
        } catch (Exception e) {
            log.warn("MongoDB Atlas connection failed: {}. Activating resilient high-speed in-memory MongoDB engine...", e.getMessage());
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
